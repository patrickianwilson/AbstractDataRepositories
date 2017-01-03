package com.patrickwilson.ardm.datasource.common;

/**
 * Exception is thrown if the entity does not specify an key information.
 */
public class NoEntityKeyException extends Exception {
    public NoEntityKeyException(Object entity) {
        super(String.format("entity %s does not specify a key", entity));
    }

    public NoEntityKeyException(Object entity, Throwable t) {
        super(String.format("entity %s does not specify a key", entity), t);
    }

    public NoEntityKeyException(Class<?> entityClazz) {
        super(String.format("entity type %s does not specify a key", entityClazz));
    }
}
