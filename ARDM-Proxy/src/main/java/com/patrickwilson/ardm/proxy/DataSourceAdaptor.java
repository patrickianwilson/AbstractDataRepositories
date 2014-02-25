package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryResult;

/**
 * A definition for definitng different datasource types (IE: JPA, or Mongo).
 * User: pwilson
 */
public interface DataSourceAdaptor {

    QueryResult findAll();

    QueryResult findByCriteria(QueryData query);

}
