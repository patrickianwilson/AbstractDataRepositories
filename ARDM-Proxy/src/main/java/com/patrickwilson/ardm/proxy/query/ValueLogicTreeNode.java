package com.patrickwilson.ardm.proxy.query;

/**
 * A logic tree node representing a concrete selector.
 * User: pwilson
 */
public class ValueLogicTreeNode {

    private String columnName;
    private Object criteriaValue;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getCriteriaValue() {
        return criteriaValue;
    }

    public void setCriteriaValue(Object criteriaValue) {
        this.criteriaValue = criteriaValue;
    }
}
