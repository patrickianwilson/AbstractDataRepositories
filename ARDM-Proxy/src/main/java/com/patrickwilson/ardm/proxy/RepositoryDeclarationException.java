package com.patrickwilson.ardm.proxy;

/**
 * Thrown if a repository interface is declaring an unknown return type.
 * User: pwilson
 */
public class RepositoryDeclarationException extends RepositoryException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public RepositoryDeclarationException() {
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RepositoryDeclarationException(String message) {
        super(message);
    }




}
