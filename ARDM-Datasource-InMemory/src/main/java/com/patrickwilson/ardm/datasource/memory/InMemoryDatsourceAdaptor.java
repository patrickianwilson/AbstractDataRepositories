package com.patrickwilson.ardm.datasource.memory;

import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;

/**
 * The in memory datasource is designed to be both a reference implementation and a
 * light weight emulator for fast and precise local development.
 *
 */
public class InMemoryDatsourceAdaptor implements QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {
    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {
        return null;
    }

    @Override
    public <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz) {

    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        return null;
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query) {
        return null;
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        return null;
    }
}
