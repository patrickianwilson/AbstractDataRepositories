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
import java.util.ArrayList;
import java.util.List;

/**
 * A logic tree node the is a composite statement.
 * User: pwilson
 */
public class LogicTreeCompositeNode extends LogicTreeNode {

    private List<LogicTreeNode> subNodes = new ArrayList<>();
    private Conjection conjection;

    /**
     * a list of conjunctions that are currently supported.
     */
    public enum Conjection { OR, AND }

    @Override
    public boolean hasSubNodes() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<LogicTreeNode> getSubNodes() {
        return subNodes;
    }

    public void addSubNode(LogicTreeNode child) {
        this.subNodes.add(child);
    }

    public Conjection getConjection() {
        return conjection;
    }

    public void setConjection(Conjection conjection) {
        this.conjection = conjection;
    }

    //CheckStyle:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicTreeCompositeNode)) return false;

        LogicTreeCompositeNode that = (LogicTreeCompositeNode) o;

        if (conjection != that.conjection) return false;
        if (subNodes != null ? !subNodes.equals(that.subNodes) : that.subNodes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subNodes != null ? subNodes.hashCode() : 0;
        result = 31 * result + (conjection != null ? conjection.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "LogicTreeCompositeNode{" +
                "subNodes=" + subNodes +
                ", conjection=" + conjection +
                '}';
    }

    //CheckStyle:ON
}
