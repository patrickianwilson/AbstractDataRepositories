package com.patrickwilson.ardm.proxy.query;
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
