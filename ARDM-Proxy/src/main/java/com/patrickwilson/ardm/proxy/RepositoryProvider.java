package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.datasource.api.exception.RepositoryInstantiationExcepiton;

import java.lang.reflect.Proxy;

/**
 * This is a convenience provider to automatically wire together the requested compination of repository interface and
 * datasource provider.
 *
 * User: pwilson
 */
public class RepositoryProvider {

    private static final DataSourceAdaptor DEFAULT_DATASOURCE_ADAPTOR = new NotImplementedDataSourceAdaptor();

    public <T> BindableRepsitoryDatasource<T> bind(Class<T> repositoryClazz) {

        try {
            BindableRepsitoryDatasource result = (BindableRepsitoryDatasource) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{BindableRepsitoryDatasource.class, repositoryClazz}, new RepositoryDynamicProxyInvocationHandler());
            return result;
        } catch (Exception e) {
            throw new RepositoryInstantiationExcepiton(e);
        }

    }

}
