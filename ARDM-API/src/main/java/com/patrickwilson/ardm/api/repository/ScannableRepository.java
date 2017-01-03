package com.patrickwilson.ardm.api.repository;


/**
 * Created by pwilson on 1/3/17.
 * @param <ENTITY> the entity type for this repository.
 *
 */
public interface ScannableRepository<ENTITY> {
    QueryResult<ENTITY> findAll();
}
