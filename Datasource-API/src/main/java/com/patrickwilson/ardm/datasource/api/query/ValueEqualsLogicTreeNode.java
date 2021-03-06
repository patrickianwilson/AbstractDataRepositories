package com.patrickwilson.ardm.datasource.api.query;
/*
 The MIT License (MIT)

 Copyright (c) 2014 Patrick Wilson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
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
