package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.datasource.api.DataSourceAdaptor;

/**
 * an interface to add to all proxy objects so that the datasource may be substituted on the fly.
 * User: pwilson
 * @param <T> typesafely covert the proxy object to the expected repository interface.
 */
public interface BindableRepsitoryDatasource<T> {

    T to(DataSourceAdaptor adaptor);

}
