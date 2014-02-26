package com.patrickwilson.ardm.api.repository;

/**
 * Basic CRUD capabilities.
 * User: pwilson
 * @param <ENTITY> for method type safety, this repository can only deal with a single entity at a time.
 */
public interface CRUDRepository<ENTITY> {

    ENTITY save(ENTITY entity);

}
