package com.patrickwilson.ardm.datasource.derby;

import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;

/**
 * An embedded derby server adaptor.
 * User: pwilson
 */
public class EmbeddedDerbyDatasourceAdaptor implements QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {


    public EmbeddedDerbyDatasourceAdaptor(String connectionInfo) {

    }



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
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
