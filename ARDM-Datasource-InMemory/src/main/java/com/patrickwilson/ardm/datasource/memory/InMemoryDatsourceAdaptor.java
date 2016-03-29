package com.patrickwilson.ardm.datasource.memory;

import java.util.HashMap;
import java.util.UUID;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;
import com.patrickwilson.ardm.datasource.common.EntityUtils;
import com.patrickwilson.ardm.datasource.common.NoEntityKeyException;

/**
 * The in memory datasource is designed to be both a reference implementation and a
 * light weight emulator for fast and precise local development.
 *
 */
public class InMemoryDatsourceAdaptor implements QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {

    private HashMap<Object, Object> mockDb = new HashMap<>();

    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {
        try {
            EntityKey key = EntityUtils.findEntityKey(entity);

            if (key.getKey() == null) {
                if ((!key.getKeyClass().equals(String.class)) && (!key.getKeyClass().equals(Object.class))) {
                    throw new RepositoryEntityException("Only String or Object key types are supported.");
                }
                String newKey = UUID.randomUUID().toString();
                EntityUtils.updateEntityKey(entity, new SimpleEnitityKey(newKey, String.class));
            }
            mockDb.put(key.getKey(), entity);
            return entity;

        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException(e);
        }
    }

    @Override
    public <ENTITY> void delete(EntityKey id, Class<ENTITY> clazz) {
        mockDb.remove(id.getKey());

    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        return (ENTITY) mockDb.get(id.getKey());
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query, Class<ENTITY> clazz) {
        return null;
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        return null;
    }

    /**
     * clear the sample database.
     */
    public void clearDatabase() {
        this.mockDb.clear();
    }
}
