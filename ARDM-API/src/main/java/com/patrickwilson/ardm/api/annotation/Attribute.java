package com.patrickwilson.ardm.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to denote an attribute on an entity.
 * User: pwilson
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
    String dbColumn() default "";
}
