package com.patrickwilson.ardm.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denote a concrete class as a repository.  Any class that is marked with this annotation will
 * have a generated proxy class created and wired in transparently.
 * User: pwilson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE })
public @interface Repository {
    Class value();
}
