package com.patrickwilson.ardm.api.key;

/**
 * An simple version of an entity key that attempts to save return a generic key in the requested format.
 * User: pwilson
 * @param <KEY_TYPE> a generic type for the actual key.
 */
public abstract class SimpleEnitityKey<KEY_TYPE> implements EntityKey<KEY_TYPE> {

    private Class<KEY_TYPE> keyTypeClass;

    SimpleEnitityKey(Class<KEY_TYPE> keyTypeClass) {
        this.keyTypeClass = keyTypeClass;
    }

    @Override
    public Class<KEY_TYPE> getKeyClass() {
        return keyTypeClass;
    }
}
