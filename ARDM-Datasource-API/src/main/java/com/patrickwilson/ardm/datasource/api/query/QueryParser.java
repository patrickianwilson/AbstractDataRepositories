package com.patrickwilson.ardm.datasource.api.query;


/**
 * Parse queryMethod names and generate query trees.
 * User: pwilson
 */
public interface QueryParser {

    QueryLogicTree parse(String query) throws InvalidMethodNameException;
}
