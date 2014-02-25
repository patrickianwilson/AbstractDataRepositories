package com.patrickwilson.ardm.proxy;

/**
 * A precanned exception describing a scenario where a repository interface was specified but not bound to a data source.
 * User: pwilson
 */
public class RepositoryAdaptorNotSpecifiedException extends RuntimeException {

    public RepositoryAdaptorNotSpecifiedException() {
        super("You must always bind a repistory to a datasource!  Please call RepositoryProvider.bind(<repository interface>).to(<datasource object>)");
    }
}
