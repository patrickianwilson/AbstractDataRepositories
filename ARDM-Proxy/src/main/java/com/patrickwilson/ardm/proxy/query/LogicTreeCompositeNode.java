package com.patrickwilson.ardm.proxy.query;

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
