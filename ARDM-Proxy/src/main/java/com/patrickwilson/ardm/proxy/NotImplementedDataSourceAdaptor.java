package com.patrickwilson.ardm.proxy;

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
    public QueryResult findAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryResult findByCriteria(QueryData query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
