package com.patrickwilson.ardm.datasource.common;

/**
 * Created by pwilson on 12/10/16.
 */
public class UnreadablePropertyException extends RuntimeException {
    public UnreadablePropertyException() {
    }

    public UnreadablePropertyException(String message) {
        super(message);
    }

    public UnreadablePropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnreadablePropertyException(Throwable cause) {
        super(cause);
    }

    public UnreadablePropertyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
