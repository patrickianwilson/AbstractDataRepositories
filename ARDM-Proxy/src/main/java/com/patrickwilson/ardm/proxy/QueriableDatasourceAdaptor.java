package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryResult;

/**
 * A definition of functionality provided by a datasource that can be queried.
 * User: pwilson
 */
public interface QueriableDatasourceAdaptor extends DataSourceAdaptor {

    <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query);
}
