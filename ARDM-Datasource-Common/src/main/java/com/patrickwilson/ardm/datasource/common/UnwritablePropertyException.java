package com.patrickwilson.ardm.datasource.common;

/**
 * Created by pwilson on 12/23/16.
 */
public class UnwritablePropertyException extends RuntimeException {
    public UnwritablePropertyException() {
    }

    public UnwritablePropertyException(String message) {
        super(message);
    }

    public UnwritablePropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnwritablePropertyException(Throwable cause) {
        super(cause);
    }

    public UnwritablePropertyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
