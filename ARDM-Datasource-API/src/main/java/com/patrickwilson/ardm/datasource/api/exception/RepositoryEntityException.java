package com.patrickwilson.ardm.datasource.api.exception;

import java.lang.reflect.Method;

/**
 * Thrown if there is a problem with the provided entity object.  Usually this is a classloader or security manager problem
 * with visiblilty of properties.
 */
public class RepositoryEntityException extends RepositoryException {
    public RepositoryEntityException() {
    }

    public RepositoryEntityException(String message) {
        super(message);
    }

    public RepositoryEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryEntityException(Class<?> entityType, Throwable cause) {
        super ("Unable to inspect entity of type [" + entityType + "].", cause);
    }

    public RepositoryEntityException(Method methodName, Class<?> entityType, Throwable cause) {
        super ("Unable to access the method " + methodName.getName() + " on entity of type [" + entityType + "]", cause);
    }

    public RepositoryEntityException(Throwable cause) {
        super(cause);
    }

    public RepositoryEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
