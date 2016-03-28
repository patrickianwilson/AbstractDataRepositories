package com.patrickwilson.ardm.proxy;

/**
 * Created by pwilson on 3/28/16.
 */
public class IncompatibleDatasourceAdaptorException extends RuntimeException {
    public IncompatibleDatasourceAdaptorException(String method, Class repositoryClass, Class datasourceClass) {
        super(String.format("The datasource %s does not provide functionality for method %s on repository interface %s",
                datasourceClass,
                method,
                repositoryClass));
    }
}
