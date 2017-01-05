package com.patrickwilson.ardm.datasource.gcp.datastore;
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
import com.google.cloud.datastore.DateTime;
import com.google.cloud.datastore.DateTimeValue;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.Value;
import com.google.common.collect.ImmutableList;
import com.patrickwilson.ardm.datasource.api.RepositoryQueryExectionException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryException;
import com.patrickwilson.ardm.datasource.api.query.LogicTreeCompositeNode;
import com.patrickwilson.ardm.datasource.api.query.LogicTreeNode;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.ValueEqualsLogicTreeNode;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pwilson on 12/28/16.
 */
public class GQLQueryGenerator {

    public EntityQuery toGQL(QueryData queryTree, String kind) {
        EntityQuery.Builder q = Query.newEntityQueryBuilder().setKind(kind);


        q.setFilter(doCondition(queryTree.getCriteria().getRootCriteria(), queryTree.getParameters()));
        if (queryTree.getPage() != null) {
            //add a LIMIT section
            if (queryTree.getPage().getNumberOfResults() != QueryPage.NOT_SET) {
                q.setLimit(queryTree.getPage().getNumberOfResults());
            }

            if (queryTree.getPage().getStartIndex() != 0) {
                q.setOffset(queryTree.getPage().getStartIndex());
            }
        }

        return q.build();
    }

    private StructuredQuery.Filter doCondition(LogicTreeNode n, Object[] args) {
        if (n instanceof ValueEqualsLogicTreeNode) {
            String name = ((ValueEqualsLogicTreeNode) n).getColumnName();

            Object val = args[((ValueEqualsLogicTreeNode) n).getValueArgIndex()];

            Value value = null;
            if (val instanceof String) {
                value = StringValue.newBuilder((String) val).build();
            } else if (val instanceof Integer) {
                value = LongValue.newBuilder(new Long((Integer) val)).build();
            } else if (val  instanceof Short) {
                value = LongValue.newBuilder(new Long((Short) val)).build();
            } else if (val instanceof Long) {
                value = LongValue.newBuilder((Long) val).build();
            } else if (val instanceof Date) {
                value = DateTimeValue.newBuilder(DateTime.copyFrom((Date) val)).build();
            } else if (val instanceof Calendar) {
                value = DateTimeValue.newBuilder(DateTime.copyFrom((Calendar) val)).build();
            } else if (val instanceof Float) {
                value = DoubleValue.newBuilder(new Double((Float) val)).build();
            } else if (val instanceof Double) {
                value = DoubleValue.newBuilder((Double) val).build();
            } else {
                //render in using toString
                value = StringValue.newBuilder(val.toString()).build();
            }

            return StructuredQuery.PropertyFilter.eq(name, value);

        }

        if (n instanceof LogicTreeCompositeNode) {
            LogicTreeCompositeNode casted = ((LogicTreeCompositeNode) n);
            if (casted.getConjection() != LogicTreeCompositeNode.Conjection.AND) {
                //OR queries not supported in Cloud Datastore.
                throw new RepositoryQueryExectionException("Invalid congunction for datastore.  Only AND queries allowed.");
            }


//            StructuredQuery.CompositeFilter.and()
            ImmutableList.Builder<StructuredQuery.Filter> childFilters = ImmutableList.builder();


            StringBuilder queryFragmentBuilder = new StringBuilder();

            HashMap<String, Object> params = new HashMap<>();
            for (LogicTreeNode child: casted.getSubNodes()) {
                childFilters.add(doCondition(child, args));
            }
            List<StructuredQuery.Filter> childFilterList = childFilters.build();
            return StructuredQuery.CompositeFilter.and(childFilterList.get(0), childFilterList.subList(1, childFilterList.size()).toArray(new StructuredQuery.Filter[0]));

        } else {
            throw new RepositoryException(String.format("Query Node type not supported: %s.  This is most likely a bug!", n.getClass().getName()));
        }

    }


}
