package com.patrickwilson.ardm.proxy.query;

import java.util.List;

/**
 * A base class for logic tree nodes.
 * User: pwilson
 */
public abstract class LogicTreeNode {

    public abstract boolean hasSubNodes();

    public abstract List<LogicTreeNode> getSubNodes();


}
