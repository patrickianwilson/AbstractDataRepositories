package com.patrickwilson.ardm.datasource.common;

/**
 * Created by pwilson on 12/22/16.
 */
public class NotAnEntityException extends RuntimeException {

    public NotAnEntityException(Class clazz, String message) {
        super(String.format("Class %s is not an entity because %s", clazz.getName(), message));
    }
}
