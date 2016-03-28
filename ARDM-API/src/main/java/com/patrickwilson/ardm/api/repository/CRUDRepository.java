package com.patrickwilson.ardm.api.repository;

import com.patrickwilson.ardm.api.key.EntityKey;

/**
 * Basic CRUD capabilities.
 * User: pwilson
 * @param <ENTITY> for method type safety, this repository can only deal with a single entity at a time.
 */
public interface CRUDRepository<ENTITY, KEY> {

    ENTITY save(ENTITY entity);

    void update(ENTITY entity);

    void delete(EntityKey<KEY> id);

    ENTITY findOne(EntityKey<KEY> id);
}
