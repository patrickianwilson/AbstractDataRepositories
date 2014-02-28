package com.patrickwilson.ardm.api.key;

/**
 * A typesafe key interface.
 * User: pwilson
 * @param <KEY_TYPE> the Type of object representing the key.
 */
public interface EntityKey<KEY_TYPE> {

    KEY_TYPE getKey();

    Class<KEY_TYPE> getKeyClass();

}
