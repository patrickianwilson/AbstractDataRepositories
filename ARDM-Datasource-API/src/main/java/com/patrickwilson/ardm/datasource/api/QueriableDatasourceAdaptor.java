package com.patrickwilson.ardm.datasource.api;

import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;

/**
 * A definition of functionality provided by a datasource that can be queried.
 * User: pwilson
 */
public interface QueriableDatasourceAdaptor extends DataSourceAdaptor {

    <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query);
}
