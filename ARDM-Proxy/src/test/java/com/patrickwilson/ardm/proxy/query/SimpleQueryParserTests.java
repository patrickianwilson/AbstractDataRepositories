package com.patrickwilson.ardm.proxy.query;

import com.patrickwilson.ardm.datasource.api.query.LogicTreeCompositeNode;
import com.patrickwilson.ardm.datasource.api.query.QueryLogicTree;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.SimpleQueryParser;
import com.patrickwilson.ardm.datasource.api.query.ValueEqualsLogicTreeNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the method query parsing.
 * User: pwilson
 */
public class SimpleQueryParserTests {


    private SimpleQueryParser underTest = new SimpleQueryParser();

    @Test
    public void tryFindByFirstName() {
        QueryLogicTree expected = findByFirstName();

        QueryLogicTree actual = underTest.parse("firstName");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tryFindByFirstNameAndLastName() {
        QueryLogicTree expected = findByFirstNameAndLastName();

        QueryLogicTree actual = underTest.parse("FirstNameAndLastName");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tryFindByIdOrFirstNameAndLastName() {
        QueryLogicTree expected = findByIdOrFirstNameAndLastName();

        QueryLogicTree actual = underTest.parse("IdOrFirstNameAndLastName");

        Assert.assertEquals(expected, actual);
    }



    private QueryLogicTree findByFirstName() {
        QueryLogicTree result = new QueryLogicTree();

        ValueEqualsLogicTreeNode rootNode = new ValueEqualsLogicTreeNode();
        rootNode.setColumnName("firstName");
        rootNode.setValueArgIndex(0);

        result.setRootCriteria(rootNode);

        return result;
    }

    private QueryLogicTree findByFirstNameAndLastName() {
        QueryLogicTree result = new QueryLogicTree();

        LogicTreeCompositeNode rootNode = new LogicTreeCompositeNode();
        rootNode.setConjection(LogicTreeCompositeNode.Conjection.AND);

        ValueEqualsLogicTreeNode fnameNode = new ValueEqualsLogicTreeNode();
        fnameNode.setColumnName("firstName");
        fnameNode.setValueArgIndex(0);
        rootNode.addSubNode(fnameNode);

        ValueEqualsLogicTreeNode lnameNode = new ValueEqualsLogicTreeNode();
        lnameNode.setColumnName("lastName");
        lnameNode.setValueArgIndex(1);
        rootNode.addSubNode(lnameNode);

        result.setRootCriteria(rootNode);

        return result;
    }

    private QueryLogicTree findByIdOrFirstNameAndLastName() {
        QueryLogicTree result = new QueryLogicTree();

        LogicTreeCompositeNode rootNode = new LogicTreeCompositeNode();
        rootNode.setConjection(LogicTreeCompositeNode.Conjection.OR);

        ValueEqualsLogicTreeNode idNode = new ValueEqualsLogicTreeNode();
        idNode.setColumnName("id");
        idNode.setValueArgIndex(0);
        rootNode.addSubNode(idNode);

        LogicTreeCompositeNode innerComp = new LogicTreeCompositeNode();
        innerComp.setConjection(LogicTreeCompositeNode.Conjection.AND);

        ValueEqualsLogicTreeNode fnameNode = new ValueEqualsLogicTreeNode();
        fnameNode.setColumnName("firstName");
        fnameNode.setValueArgIndex(1);
        innerComp.addSubNode(fnameNode);

        ValueEqualsLogicTreeNode lnameNode = new ValueEqualsLogicTreeNode();
        lnameNode.setColumnName("lastName");
        lnameNode.setValueArgIndex(2);
        innerComp.addSubNode(lnameNode);

        rootNode.addSubNode(innerComp);

        result.setRootCriteria(rootNode);

        return result;
    }



    private QueryPage getStandardQueryPage() {
        return new QueryPage();
    }

}
