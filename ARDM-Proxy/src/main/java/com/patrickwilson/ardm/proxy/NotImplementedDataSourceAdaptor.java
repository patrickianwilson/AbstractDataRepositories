package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.DataSourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.api.repository.QueryResult;

/**
 * This adaptor is deliberately created to make the API more readible. It does nothing except prevent configuration
 * problems.
 *
 * User: pwilson
 */
public class NotImplementedDataSourceAdaptor implements DataSourceAdaptor, QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {
    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {
        return null;
    }

    @Override
    public <ENTITY> void delete(EntityKey id, Class<ENTITY> clazz) {

    }

    @Override
    public <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz) {

    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        return null;
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query, Class<ENTITY> clazz) {
        return null;
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        return null;
    }
}
