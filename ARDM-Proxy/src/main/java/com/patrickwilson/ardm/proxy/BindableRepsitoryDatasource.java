package com.patrickwilson.ardm.proxy;

/**
 * an interface to add to all proxy objects so that the datasource may be substituted on the fly.
 * User: pwilson
 */
public interface BindableRepsitoryDatasource<T> {

    T to(DataSourceAdaptor adaptor);

}
