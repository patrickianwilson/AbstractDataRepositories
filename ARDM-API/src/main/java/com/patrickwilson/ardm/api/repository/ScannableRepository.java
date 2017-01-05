package com.patrickwilson.ardm.api.repository;


/**
 * Created by pwilson on 1/3/17.
 * @param <ENTITY> the entity type for this repository.
 * @param <KEY> the type of key that can be used to scan.
 */
public interface ScannableRepository<ENTITY, KEY> {
    QueryResult<ENTITY> findAll();
    QueryResult<ENTITY> findAllWithKeyPrefix(KEY prefix);

    /**
     * build a new entity key using the provided prefix/parent as a reference.
     * @param prefix
     * @return
     */
    KEY buildPrefixKey(KEY prefix);
}
