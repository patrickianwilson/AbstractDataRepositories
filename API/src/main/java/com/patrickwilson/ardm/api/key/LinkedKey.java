package com.patrickwilson.ardm.api.key;

/**
 * Created by pwilson on 1/13/17.
 */
public interface LinkedKey extends EntityKey {
    /**
     * Get the parent (depends on the implementation) of this key.
     * @return the parent key.
     */
    EntityKey getLinkedKey();
}

