package com.patrickwilson.ardm.proxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Query;
import com.patrickwilson.ardm.api.annotation.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Magic invocation handler for dynamic data repository proxy instances.
 * User: pwilson
 */
public class RepositoryDynamicProxyInvocationHandler implements InvocationHandler {

    private Map<String, Method> passthroughMethods = null;
    private DataSourceAdaptor adaptor;

    public static final List<String> BASE_METHODS = Lists.newArrayList("toString", "hashCode", "clone", "finalize", "equals", "wait", "notify", "notifyAll");

    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return the value to return from the method invocation on the
     *         proxy instance.  If the declared return type of the interface
     *         method is a primitive type, then the value returned by
     *         this method must be an instance of the corresponding primitive
     *         wrapper class; otherwise, it must be a type assignable to the
     *         declared return type.  If the value returned by this method is
     *         {@code null} and the interface method's return type is
     *         primitive, then a {@code NullPointerException} will be
     *         thrown by the method invocation on the proxy instance.  If the
     *         value returned by this method is otherwise not compatible with
     *         the interface method's declared return type as described above,
     *         a {@code ClassCastException} will be thrown by the method
     *         invocation on the proxy instance.
     * @throws Throwable the exception to throw from the method
     *                   invocation on the proxy instance.  The exception's type must be
     *                   assignable either to any of the exception types declared in the
     *                   {@code throws} clause of the interface method or to the
     *                   unchecked exception types {@code java.lang.RuntimeException}
     *                   or {@code java.lang.Error}.  If a checked exception is
     *                   thrown by this method that is not assignable to any of the
     *                   exception types declared in the {@code throws} clause of
     *                   the interface method, then an
     *                   {@link java.lang.reflect.UndeclaredThrowableException} containing the
     *                   exception that was thrown by this method will be thrown by the
     *                   method invocation on the proxy instance.
     * @see java.lang.reflect.UndeclaredThrowableException
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getAnnotation(Query.class) != null) {
            //this is a query invocation.
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

            Class[] allInterfaces = proxy.getClass().getInterfaces();

            Class entityType = null;
            for (Class type: allInterfaces) {
                if (type.getAnnotation(Repository.class) != null) {
                   entityType = ((Repository) type.getAnnotation(Repository.class)).value();
                }
            }
            if (entityType == null) {
                throw new RepositoryInvocationException("No @Repository annotation can be found on any of the repository interfaces.");
            }

            passThroughArgs[args.length] = entityType;

            try {
                return passthroughMethod.invoke(this.adaptor, passThroughArgs);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RepositoryInvocationException(e);
            } catch (InvocationTargetException e) {
                throw new RepositoryInteractionException(e.getTargetException());
            }
        }

        return null;

    }


    private void bindDatastoreAdaptor(DataSourceAdaptor adaptor) {
        this.adaptor = adaptor;

        ImmutableMap.Builder<String, Method> methodMapBuilder = ImmutableMap.<String, Method>builder();

        Method[] adaptorReflectionMethods = DataSourceAdaptor.class.getMethods();
        for (Method method: adaptorReflectionMethods) {
            if (!BASE_METHODS.contains(method.getName())) {
               methodMapBuilder.put(method.getName(), method);
            }
        }

        this.passthroughMethods = methodMapBuilder.build();

    }
}
