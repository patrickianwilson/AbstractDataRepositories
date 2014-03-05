package com.patrickwilson.ardm.proxy.query;

/**
 * A generic style of query.  This is deliberately not a SQL flavour.  These Queries support Basic CRUD operations
 * and have the capacity to specify a few simple logical selectors.
 * User: pwilson
 */
public class QueryData {

    private QueryPage page = new QueryPage();
    private QueryLogicTree criteria;

    public QueryData(QueryPage page, QueryLogicTree criteria) {
        this.page = page;
        this.criteria = criteria;
    }

    public QueryData() {

    }

    public QueryLogicTree getCriteria() {
        return criteria;
    }

    public void setCriteria(QueryLogicTree criteria) {
        this.criteria = criteria;
    }

    public QueryPage getPage() {
        return page;
    }

    public void setPage(QueryPage page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryData)) return false;

        QueryData queryData = (QueryData) o;

        if (criteria != null ? !criteria.equals(queryData.criteria) : queryData.criteria != null) return false;
        if (page != null ? !page.equals(queryData.page) : queryData.page != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = page != null ? page.hashCode() : 0;
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        return result;
    }
}
