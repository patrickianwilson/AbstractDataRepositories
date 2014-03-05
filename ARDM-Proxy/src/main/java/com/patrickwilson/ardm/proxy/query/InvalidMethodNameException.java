package com.patrickwilson.ardm.proxy.query;

import com.patrickwilson.ardm.proxy.RepositoryException;

/**
 * Thrown if the method name cannot be parsed into a valid query tree.
 * User: pwilson
 */
public class InvalidMethodNameException extends RepositoryException {


    public InvalidMethodNameException(String query) {
        super("The provided query method segment cannot be converted into a query tree: " + query);
    }

    public InvalidMethodNameException(String reason, String query) {
        super(reason + " full query: " + query);
    }
}
