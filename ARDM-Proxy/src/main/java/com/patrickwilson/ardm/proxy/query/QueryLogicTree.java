package com.patrickwilson.ardm.proxy.query;

/**
 * House the basic logical query tree.
 * User: pwilson
 */
public class QueryLogicTree {

    private LogicTreeNode rootCriteria;

    public LogicTreeNode getRootCriteria() {
        return rootCriteria;
    }

    public void setRootCriteria(LogicTreeNode rootCriteria) {
        this.rootCriteria = rootCriteria;
    }
}
