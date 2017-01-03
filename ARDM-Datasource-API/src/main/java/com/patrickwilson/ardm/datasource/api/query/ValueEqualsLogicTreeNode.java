package com.patrickwilson.ardm.datasource.api.query;

import java.util.List;

/**
 * A logic tree node representing a concrete selector.
 * User: pwilson
 */
public class ValueEqualsLogicTreeNode extends LogicTreeNode {

    private String columnName;
    private int valueArgIndex;

    public ValueEqualsLogicTreeNode() {
    }

    public ValueEqualsLogicTreeNode(String columnName, int valueArgIndex) {
        this.columnName = columnName;
        this.valueArgIndex = valueArgIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getValueArgIndex() {
        return valueArgIndex;
    }

    public void setValueArgIndex(int valueArgIndex) {
        this.valueArgIndex = valueArgIndex;
    }

    @Override
    public boolean hasSubNodes() {
        return false;
    }

    @Override
    public List<LogicTreeNode> getSubNodes() {
        return null;
    }

    //CheckStyle:OFF

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueEqualsLogicTreeNode)) return false;

        ValueEqualsLogicTreeNode that = (ValueEqualsLogicTreeNode) o;

        if (valueArgIndex != that.valueArgIndex) return false;
        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + valueArgIndex;
        return result;
    }

    @Override
    public String toString() {
        return "ValueLogicTreeNode{" +
                "columnName='" + columnName + '\'' +
                ", valueArgIndex=" + valueArgIndex +
                '}';
    }

    //CheckStyle:ON

}
