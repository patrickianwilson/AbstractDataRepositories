package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryResult;

/**
 * This adaptor is deliberately created to make the API more readible. It does nothing except prevent configuration
 * problems.
 *
 * User: pwilson
 */
public class NotImplementedDataSourceAdaptor implements DataSourceAdaptor {

    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
