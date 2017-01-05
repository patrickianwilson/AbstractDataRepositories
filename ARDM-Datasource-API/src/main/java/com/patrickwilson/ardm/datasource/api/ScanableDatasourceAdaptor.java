package com.patrickwilson.ardm.datasource.api;


import com.patrickwilson.ardm.api.repository.QueryResult;

/**
 * A definition of functionality that a scannable data source can provide.
 * User: pwilson
 */
public interface ScanableDatasourceAdaptor extends DataSourceAdaptor {

    <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz);

    <ENTITY, KEY> QueryResult<ENTITY> findAllWithKeyPrefix(KEY prefix, Class<ENTITY> clazz);

    <ENTITY, KEY> KEY buildPrefixKey(Object prefix, Class<ENTITY> clazz);
}
