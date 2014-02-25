package com.patrickwilson.ardm.proxy.query;

/**
 * A generic style of query.  This is deliberately not a SQL flavour.  These Queries support Basic CRUD operations
 * and have the capacity to specify a few simple logical selectors.
 * User: pwilson
 */
public class QueryData {

    /**
     * specifify the intenet of the query.
     */
    public static enum QueryMethod {
        FIND_ALL, FIND_BY_CRITERIA;
    }

    private QueryMethod method;

    private QueryLogicTree criteria;

    public QueryMethod getMethod() {
        return method;
    }

    public void setMethod(QueryMethod method) {
        this.method = method;
    }

    public QueryLogicTree getCriteria() {
        return criteria;
    }

    public void setCriteria(QueryLogicTree criteria) {
        this.criteria = criteria;
    }
}
