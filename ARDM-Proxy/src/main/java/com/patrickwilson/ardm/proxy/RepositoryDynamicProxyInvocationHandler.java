package com.patrickwilson.ardm.proxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Query;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryPage;
import com.patrickwilson.ardm.proxy.query.QueryParser;
import com.patrickwilson.ardm.proxy.query.QueryResult;
import com.patrickwilson.ardm.proxy.query.SimpleQueryParser;

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

    private Map<String, Method> passthroughMethods = null;
    private Map<String, Method> queryMethods = null;
    private DataSourceAdaptor adaptor;
    private QueryParser queryParser = new SimpleQueryParser();

    public static final List<String> BASE_METHODS = Lists.newArrayList("toString", "hashCode", "clone", "finalize", "equals", "wait", "notify", "notifyAll");

    public static final List<String> QUERY_METHODS = Lists.newArrayList("findByCriteria", "findAll");

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

            String query = queryMethodMatcher.group(1);


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
        } else if (this.passthroughMethods.get(method.getName()) != null) {
            //this method can be directed straight at the
            Method passthroughMethod = passthroughMethods.get(method.getName());

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

        return null;

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

        ImmutableMap.Builder<String, Method> methodMapBuilder = ImmutableMap.<String, Method>builder();
        ImmutableMap.Builder<String, Method> queryMapBuilder = ImmutableMap.<String, Method>builder();

        Method[] adaptorReflectionMethods = DataSourceAdaptor.class.getMethods();
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
