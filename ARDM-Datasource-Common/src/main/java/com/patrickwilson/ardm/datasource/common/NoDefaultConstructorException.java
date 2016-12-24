package com.patrickwilson.ardm.datasource.common;

/**
 * Created by pwilson on 12/23/16.
 */
public class NoDefaultConstructorException extends RuntimeException {
    public NoDefaultConstructorException(Class entityClass) {
        super(String.format("No suitable default constructor found on entity class %s", entityClass.getName()));
    }

    public NoDefaultConstructorException(Class entityClass, Throwable t) {
        super(String.format("No suitable default constructor found on entity class %s", entityClass.getName()), t);
    }
}
