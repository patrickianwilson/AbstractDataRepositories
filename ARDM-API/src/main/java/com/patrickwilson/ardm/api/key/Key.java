package com.patrickwilson.ardm.api.key;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by pwilson on 3/29/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
    Class keyClass() default Object.class;
}
