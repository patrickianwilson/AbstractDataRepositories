package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryResult;


/**
 * A definition for definitng different datasource types (IE: JPA, or Mongo).
 * User: pwilson
 */
public interface DataSourceAdaptor {

    <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz);

    <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz);

    <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz);

    <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz);

    <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query);

}
