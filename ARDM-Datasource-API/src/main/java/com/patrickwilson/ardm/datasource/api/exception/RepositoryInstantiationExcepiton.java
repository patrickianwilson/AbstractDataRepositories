package com.patrickwilson.ardm.datasource.api.exception;

/**
 * Thrown if the repository proxy cannot be instantiated for some reason.
 * User: pwilson
 */
public class RepositoryInstantiationExcepiton extends RuntimeException {


    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public RepositoryInstantiationExcepiton(Throwable cause) {
        super("Unable to create repository Proxy object.", cause);
    }
}
