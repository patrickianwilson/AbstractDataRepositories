package com.patrickwilson.ardm.datasource.api;

/**
 * Created by pwilson on 12/28/16.
 */
public class RepositoryQueryExectionException extends RuntimeException {
    public RepositoryQueryExectionException() {
    }

    public RepositoryQueryExectionException(String message) {
        super(message);
    }

    public RepositoryQueryExectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryQueryExectionException(Throwable cause) {
        super(cause);
    }

    public RepositoryQueryExectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
