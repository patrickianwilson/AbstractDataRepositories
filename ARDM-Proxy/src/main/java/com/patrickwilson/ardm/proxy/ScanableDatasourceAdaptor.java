package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.proxy.query.QueryResult;

/**
 * A definition of functionality that a scannable data source can provide.
 * User: pwilson
 */
public interface ScanableDatasourceAdaptor extends DataSourceAdaptor {

    <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz);

}
