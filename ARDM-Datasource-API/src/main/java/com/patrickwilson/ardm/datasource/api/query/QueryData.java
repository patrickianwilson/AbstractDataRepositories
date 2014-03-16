package com.patrickwilson.ardm.datasource.api.query;

import java.util.Arrays;

/**
 * A generic style of query.  This is deliberately not a SQL flavour.  These Queries support Basic CRUD operations
 * and have the capacity to specify a few simple logical selectors.
 * User: pwilson
 */
public class QueryData {

    private QueryPage page = new QueryPage();
    private QueryLogicTree criteria;
    private Object[] parameters;

    public QueryData(QueryPage page, QueryLogicTree criteria) {
        this.page = page;
        this.criteria = criteria;
    }

    public QueryData(QueryPage page, QueryLogicTree criteria, Object[] parameters) {
        this.page = page;
        this.criteria = criteria;
        this.parameters = parameters;
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

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    //CheckStyle:OFF

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryData)) return false;

        QueryData queryData = (QueryData) o;

        if (criteria != null ? !criteria.equals(queryData.criteria) : queryData.criteria != null) return false;
        if (page != null ? !page.equals(queryData.page) : queryData.page != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(parameters, queryData.parameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = page != null ? page.hashCode() : 0;
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        result = 31 * result + (parameters != null ? Arrays.hashCode(parameters) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QueryData{" +
                "page=" + page +
                ", criteria=" + criteria +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

    //CheckStyle:ON
}
