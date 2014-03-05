package com.patrickwilson.ardm.proxy.query;

import java.lang.reflect.Method;

/**
 * Parse queryMethod names and generate query trees.
 * User: pwilson
 */
public interface QueryParser {

    QueryLogicTree parse(String query) throws InvalidMethodNameException;
}
