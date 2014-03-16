package com.patrickwilson.ardm.datasource.api;


import com.patrickwilson.ardm.api.key.EntityKey;

/**
 * Functionality for a basic CRUD data source.
 * User: pwilson
 */
public interface CRUDDatasourceAdaptor extends DataSourceAdaptor {

    <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz);

    <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz);

    <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz);
}
