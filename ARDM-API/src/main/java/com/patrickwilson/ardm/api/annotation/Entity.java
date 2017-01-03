package com.patrickwilson.ardm.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * denote an entity object.
 * User: pwilson
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String NO_DOMAIN_OR_TABLE = "_NULL_";
    String domainOrTable() default NO_DOMAIN_OR_TABLE;
}
