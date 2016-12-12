package com.patrickwilson.ardm.datasource.api.query;

import com.google.common.base.Strings;


/**
 * Simple implementation of a query parser.  Only handles
 * User: pwilson
 *
 * TODO - Is this algo as horrible as I think?  Need to do lots of tests to prove yes or no.
 *
 * TODO - Also no support for query clauses that are order aware (for instance findByCountGT) which should be doing a "where count > ${arg}"
 */
public class SimpleQueryParser implements QueryParser {
    private int argCounter = -1;

    @Override
    public QueryLogicTree parse(final String query) throws InvalidMethodNameException {

        String lowerCaseQuery = query; //.toLowerCase();
        //
        String[] orParts = lowerCaseQuery.split("Or");

        LogicTreeNode rootNode = null;

        if (orParts.length > 1) {

            rootNode = new LogicTreeCompositeNode();

            for (String orPart: orParts) {
                if (Strings.isNullOrEmpty(orPart)) {
                    throw new InvalidMethodNameException("Invalid OR conjunction, missing a side", query);
                }
                ((LogicTreeCompositeNode) rootNode).addSubNode(doOr(orPart, query));
            }
            ((LogicTreeCompositeNode) rootNode).setConjection(LogicTreeCompositeNode.Conjection.OR);
        } else {
            rootNode = doOr(lowerCaseQuery, lowerCaseQuery);
        }

        QueryLogicTree result = new QueryLogicTree();
        result.setRootCriteria(rootNode);
        return result;
    }

    private LogicTreeNode doOr(String orPart, String fullQuery) {

        //see if there is a seperate break down.
        String[] andParts = orPart.split("And");
        LogicTreeNode orPartNode = null;

        if (andParts.length > 1) {
            orPartNode = new LogicTreeCompositeNode();

            for (String andPart: andParts) {
                if (Strings.isNullOrEmpty(andPart)) {
                    throw new InvalidMethodNameException("Invalid AND conjunction, missing a side: " + orPart, fullQuery);
                }
                ((LogicTreeCompositeNode) orPartNode).addSubNode(doAnd(andPart));
            }
            ((LogicTreeCompositeNode) orPartNode).setConjection(LogicTreeCompositeNode.Conjection.AND);
        } else {
           orPartNode = doAnd(orPart);
        }

        return orPartNode;
    }

    private LogicTreeNode doAnd(String andPart) {

        ValueEqualsLogicTreeNode leafNode = new ValueEqualsLogicTreeNode();
        String columnName = andPart.substring(0, 1).toLowerCase() + andPart.substring(1);
        leafNode.setColumnName(columnName);
        leafNode.setValueArgIndex(++argCounter);

        return leafNode;

    }

}
