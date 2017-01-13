package com.patrickwilson.ardm.proxy;
/*
 The MIT License (MIT)

 Copyright (c) 2014 Patrick Wilson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.patrickwilson.ardm.api.annotation.Query;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.DataSourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.RepositoryQueryExectionException;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryInteractionException;
import com.patrickwilson.ardm.datasource.api.query.InvalidMethodNameException;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryLogicTree;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.QueryParser;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.ardm.datasource.api.query.SimpleQueryParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Magic invocation handler for dynamic data repository proxy instances.
 * User: pwilson
 */
public class RepositoryDynamicProxyInvocationHandler implements InvocationHandler {

    private Multimap<String, Method> passthroughMethods = null;
    private Map<String, Method> queryMethods = null;
    private DataSourceAdaptor adaptor;

    public static final List<String> BASE_METHODS = Lists.newArrayList("toString", "hashCode", "clone", "finalize", "equals", "wait", "notify", "notifyAll");


    public static final List<String> QUERY_METHODS = Lists.newArrayList("findAll");


    public static final Pattern BASIC_QUERY_METHOD_PATTERN = Pattern.compile("findBy(.*)");


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (args == null) {
            args = new Object[]{};
        }

        if (method == null) {
            //I can't see this happening
            return null;
        }

        Class entityType = getRepositoryInterface(proxy);

        if (method.getAnnotation(Query.class) != null) {
            //this is a query invocation.
            Matcher queryMethodMatcher = BASIC_QUERY_METHOD_PATTERN.matcher(method.getName());

            if (!queryMethodMatcher.matches()) {
                throw new InvalidMethodNameException("The query method does not have a valid query name", method.getName());
            }

            String query = queryMethodMatcher.group(1);

            //query parser is neither threadsafe nor reusable...
            QueryParser queryParser = new SimpleQueryParser();
            QueryLogicTree queryTree = queryParser.parse(query);

            if (this.adaptor instanceof QueriableDatasourceAdaptor) {
                QueryData queryData = new QueryData();
                QueryPage page = new QueryPage();
                queryData.setParameters(args);

                if (entityType.isAssignableFrom(method.getReturnType())) {
                    //repository method is a List return type.
                    page.setNumberOfResults(1);
                }

                queryData.setPage(page);
                queryData.setCriteria(queryTree);
                QueryResult result;
                try {
                     result = ((QueriableDatasourceAdaptor) this.adaptor).findByCriteria(queryData, entityType);
                } catch (RepositoryQueryExectionException e) {
                    //invalid query.  re-throw this with the query method name to help with troubleshooting.
                    throw new RepositoryQueryExectionException(String.format("Error executing Query method: %s", method.getName()), e);
                }
                if (Collection.class.isAssignableFrom(method.getReturnType())) {
                    //repository method is a List return type.
                    return result.getResults();
                } else if (QueryResult.class.isAssignableFrom(method.getReturnType())) {
                    return result;
                } else if (entityType.isAssignableFrom(method.getReturnType())) {
                    //can return null if no entity is actually found.
                    return result.getResults().get(0);
                } else {
                    throw new RepositoryDeclarationException("Invalid return type from method: " + method.getName() + ", return type " + method.getReturnType().getName() + " is not valid. Should be java.util.List or QueryResult object.");

                }
            }


            return null;
        } else if ("to".equals(method.getName())) {
            if (args.length == 1) {
                if (args[0] != null && args[0] instanceof DataSourceAdaptor) {
                    bindDatastoreAdaptor((DataSourceAdaptor) args[0]);
                    return proxy;
                }
            }

        } else if (this.passthroughMethods == null) {
            throw new RepositoryAdaptorNotSpecifiedException();
        } else if (this.passthroughMethods.get(method.getName()) != null && this.passthroughMethods.get(method.getName()).size() > 0) {
            //this method can be directed straight at the
            Collection<Method> passthroughMethods = this.passthroughMethods.get(method.getName());

            Method passthroughMethod = null;
            for (Method m: passthroughMethods) {
                boolean foundMatch = false;
                if ((m.getParameterTypes().length - 1) != method.getParameterTypes().length) {
                    continue;
                }
                for (int paramNum = 0; paramNum < method.getParameterTypes().length; paramNum++) {
                    if (method.getParameterTypes()[paramNum].equals(m.getParameterTypes()[paramNum])) {
                        foundMatch = true;
                        break; //param mismatch.  not the passthough.  //try the next method instead.
                    }
                }
                if (foundMatch) {
                    passthroughMethod = m;
                    break;
                }
            }

            if (passthroughMethod == null) {
                throw new RepositoryDeclarationException("Method " + method.getName() +  " cannot be bound to Abstract Repository.  It appears there is no method with matching parameters on the repository adaptor.");
            }

            //we always tag on the class object as a final param to help the datasource adaptor avoid generics complexity.
            Object[] passThroughArgs = Arrays.copyOf(args, args.length + 1);
            passThroughArgs[args.length] = entityType;

            return  invokeDataSource(passthroughMethod, passThroughArgs);

        } else if (this.queryMethods.get(method.getName()) != null) {
            //this is a query style of method.
            Method queryPassthoughMethod = queryMethods.get(method.getName());
            Object[] passthroughArgs = Arrays.copyOf(args, args.length + 1);
            passthroughArgs[args.length] = entityType;

            QueryResult result = (QueryResult) invokeDataSource(queryPassthoughMethod, passthroughArgs);

            if (Collection.class.isAssignableFrom(method.getReturnType())) {
                //repository method is a List return type.
                return result.getResults();
            } else if (QueryResult.class.isAssignableFrom(method.getReturnType())) {
                return result;
            } else {
                throw new RepositoryDeclarationException("Invalid return type from method: " + method.getName() + ", return type " + method.getReturnType().getName() + " is not valid. Should be java.util.List or QueryResult object.");

            }
        }

        throw new RepositoryDeclarationException("Method " + method.getName() +  " cannot be bound to Abstract Repository");


    }

    private Class getRepositoryInterface(Object proxy) {
        Class[] allInterfaces = proxy.getClass().getInterfaces();

        Class entityType = null;
        for (Class type: allInterfaces) {
            if (type.getAnnotation(Repository.class) != null) {
                entityType = ((Repository) type.getAnnotation(Repository.class)).value();
            }
        }

        return entityType;
    }

    private Object invokeDataSource(Method passthroughMethod, Object[] passThroughArgs) {
        try {
            return passthroughMethod.invoke(this.adaptor, passThroughArgs);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RepositoryInvocationException(e);
        } catch (InvocationTargetException e) {
            throw new RepositoryInteractionException(e.getTargetException());
        }
    }

    private void bindDatastoreAdaptor(DataSourceAdaptor adaptor) {
        this.adaptor = adaptor;

        ImmutableMultimap.Builder<String, Method> methodMapBuilder = ImmutableMultimap.<String, Method>builder();
        ImmutableMap.Builder<String, Method> queryMapBuilder = ImmutableMap.<String, Method>builder();

        Method[] adaptorReflectionMethods = CRUDDatasourceAdaptor.class.getMethods();
        for (Method method: adaptorReflectionMethods) {
           if (!BASE_METHODS.contains(method.getName())) {
               methodMapBuilder.put(method.getName(), method);
            }
        }


        adaptorReflectionMethods = ScanableDatasourceAdaptor.class.getMethods();
        for (Method method: adaptorReflectionMethods) {
            if (QUERY_METHODS.contains(method.getName())) {
                queryMapBuilder.put(method.getName(), method);
            } else if (!BASE_METHODS.contains(method.getName())) {
                methodMapBuilder.put(method.getName(), method);
            }
        }


        this.passthroughMethods = methodMapBuilder.build();
        this.queryMethods = queryMapBuilder.build();

    }
}
