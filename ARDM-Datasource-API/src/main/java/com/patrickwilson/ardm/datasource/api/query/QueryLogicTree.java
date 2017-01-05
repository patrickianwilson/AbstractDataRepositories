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
