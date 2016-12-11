package com.patrickwilson.ardm.datasource.api.exception;

/**
 * Created by pwilson on 12/10/16.
 */
public class NoSuchEntityRepositoryException extends RepositoryException {
    public NoSuchEntityRepositoryException(Class entityType) {
        super("No table provisioned for entity type: " + entityType);
    }
}
