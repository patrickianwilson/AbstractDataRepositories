package com.patrickwilson.ardm.api.key;

/**
 * Created by pwilson on 1/13/17.
 * @param <KEY_TYPE> the raw type of the underlying key.
 */
public interface LinkedKey<KEY_TYPE> extends EntityKey<KEY_TYPE> {
    /**
     * Get the parent (depends on the implementation) of this key.
     * @return the parent key.
     */
    EntityKey<?> getLinkedKey();
}

