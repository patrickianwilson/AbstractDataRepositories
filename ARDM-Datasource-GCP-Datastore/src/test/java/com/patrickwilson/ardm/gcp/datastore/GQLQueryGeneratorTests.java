package com.patrickwilson.ardm.gcp.datastore;

import com.patrickwilson.ardm.datasource.api.RepositoryQueryExectionException;
import com.patrickwilson.ardm.datasource.api.query.*;
import com.patrickwilson.ardm.datasource.gcp.datastore.GQLQueryGenerator;
import com.patrickwilson.ardm.datasource.gcp.datastore.StringBasedPreparedStatement;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pwilson on 12/28/16.
 */
public class GQLQueryGeneratorTests {
    /*
    GQLQueryGenerator underTest = new GQLQueryGenerator();

    @Test
    public void simpleSingleFilterQuery() {

        ValueEqualsLogicTreeNode inner = new ValueEqualsLogicTreeNode();
        inner.setColumnName("fname");
        inner.setValueArgIndex(0);

        QueryLogicTree query = new QueryLogicTree();
        query.setRootCriteria(inner);

        QueryData data = new QueryData();
        data.setCriteria(query);
        data.setParameters(new Object[]{"Patrick"});

        StringBasedPreparedStatement result = underTest.toGQL(data);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getQuery());
        Assert.assertNotNull(result.getParameters());

        Assert.assertEquals("WHERE fname = :fname", result.getQuery());
        Assert.assertEquals("Patrick", result.getParameters().get("fname"));


    }

    @Test
    public void simpleLimitSingleFilterQuery() {

        ValueEqualsLogicTreeNode inner = new ValueEqualsLogicTreeNode();
        inner.setColumnName("fname");
        inner.setValueArgIndex(0);

        QueryLogicTree query = new QueryLogicTree();
        query.setRootCriteria(inner);

        QueryData data = new QueryData();
        data.setCriteria(query);
        data.setParameters(new Object[]{"Patrick"});
        data.setPage(new QueryPage(10, 5));

        StringBasedPreparedStatement result = underTest.toGQL(data);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getQuery());
        Assert.assertNotNull(result.getParameters());

        Assert.assertEquals("WHERE fname = :fname LIMIT 5 OFFSET 10", result.getQuery());
        Assert.assertEquals("Patrick", result.getParameters().get("fname"));


    }

    @Test
    public void complexANDQuery() {

        ValueEqualsLogicTreeNode lnameClause = new ValueEqualsLogicTreeNode();
        lnameClause.setColumnName("lname");
        lnameClause.setValueArgIndex(1);


        ValueEqualsLogicTreeNode fnameClause = new ValueEqualsLogicTreeNode();
        fnameClause.setColumnName("fname");
        fnameClause.setValueArgIndex(0);

        LogicTreeCompositeNode andClause = new LogicTreeCompositeNode();
        andClause.setConjection(LogicTreeCompositeNode.Conjection.AND);
        andClause.addSubNode(fnameClause);
        andClause.addSubNode(lnameClause);

        QueryLogicTree query = new QueryLogicTree();
        query.setRootCriteria(andClause);

        QueryData data = new QueryData();
        data.setCriteria(query);
        data.setParameters(new Object[]{"Patrick", "Wilson"});
        data.setPage(new QueryPage(10, 5));

        StringBasedPreparedStatement result = underTest.toGQL(data);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getQuery());
        Assert.assertNotNull(result.getParameters());

        Assert.assertEquals("WHERE fname = :fname AND lname = :lname LIMIT 5 OFFSET 10", result.getQuery());
        Assert.assertEquals("Patrick", result.getParameters().get("fname"));
        Assert.assertEquals("Wilson", result.getParameters().get("lname"));

    }

    @Test(expected = RepositoryQueryExectionException.class)
    public void orQueryShouldFail() {

        ValueEqualsLogicTreeNode lnameClause = new ValueEqualsLogicTreeNode();
        lnameClause.setColumnName("lname");
        lnameClause.setValueArgIndex(1);


        ValueEqualsLogicTreeNode fnameClause = new ValueEqualsLogicTreeNode();
        fnameClause.setColumnName("fname");
        fnameClause.setValueArgIndex(0);

        LogicTreeCompositeNode andClause = new LogicTreeCompositeNode();
        andClause.setConjection(LogicTreeCompositeNode.Conjection.OR);
        andClause.addSubNode(fnameClause);
        andClause.addSubNode(lnameClause);

        QueryLogicTree query = new QueryLogicTree();
        query.setRootCriteria(andClause);

        QueryData data = new QueryData();
        data.setCriteria(query);
        data.setParameters(new Object[]{"Patrick", "Wilson"});
        data.setPage(new QueryPage(10, 5));

        StringBasedPreparedStatement result = underTest.toGQL(data);


    }
    */
}