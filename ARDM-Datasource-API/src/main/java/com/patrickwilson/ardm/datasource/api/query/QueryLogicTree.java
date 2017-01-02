package com.patrickwilson.ardm.datasource.api.query;

/**
 * House the basic logical query tree.
 * User: pwilson
 */
public class QueryLogicTree {

    public QueryLogicTree(LogicTreeNode rootCriteria) {
        this.rootCriteria = rootCriteria;
    }

    public QueryLogicTree() {
    }

    private LogicTreeNode rootCriteria;

    public LogicTreeNode getRootCriteria() {
        return rootCriteria;
    }

    public void setRootCriteria(LogicTreeNode rootCriteria) {
        this.rootCriteria = rootCriteria;
    }

    //CheckStyle:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryLogicTree)) return false;

        QueryLogicTree that = (QueryLogicTree) o;

        if (rootCriteria != null ? !rootCriteria.equals(that.rootCriteria) : that.rootCriteria != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rootCriteria != null ? rootCriteria.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "QueryLogicTree{" +
                "rootCriteria=" + rootCriteria +
                '}';
    }
    //CheckStyle:ON
}
