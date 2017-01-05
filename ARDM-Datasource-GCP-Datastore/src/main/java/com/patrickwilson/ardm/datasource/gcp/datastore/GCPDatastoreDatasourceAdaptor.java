package com.patrickwilson.ardm.datasource.gcp.datastore;


import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.ValueBuilder;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryInteractionException;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.ardm.datasource.common.EntityUtils;
import com.patrickwilson.ardm.datasource.common.NoEntityKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptor implements QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {

    public static final String TYPE_ATTRIBUTE_NAME = "_clazz";
    private Datastore datastoreClient;
    public static final Logger LOG = LoggerFactory.getLogger(GCPDatastoreDatasourceAdaptor.class);

    private Map<Class, KeyFactory> keyFactories = new HashMap<>();

    private GQLQueryGenerator queryGenerator = new GQLQueryGenerator();

    public GCPDatastoreDatasourceAdaptor(Datastore datastoreClient) {
        Preconditions.checkNotNull(datastoreClient);

        this.datastoreClient = datastoreClient;
    }

    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {

        try {
            EntityKey key = EntityUtils.findEntityKey(entity);

            if (key == null) {
                //means the key was of the type "EntityKey" but we don't know how to auto generate keys..\
                throw new RepositoryEntityException("Only String key types are supported.");
            }
            if (key.getKeyClass() == null || !key.getKeyClass().isAssignableFrom(Key.class)) {
                throw new RepositoryEntityException("Only com.google.cloud.datastore.Key entity keys are supported.");
            }

            if (key.getKey() == null) {
                KeyFactory entityKeyFactory = getKeyFactory(clazz);
                IncompleteKey datastoreKey = entityKeyFactory.newKey();
                key = new SimpleEnitityKey(datastoreKey, key.getKeyClass());
            }

            Map<String, Object> entityProperties = EntityUtils.fetchAllProperties(entity);
            Set<String> indexedProperties = EntityUtils.getchIndexablePropertyNames(entity);


            FullEntity.Builder datastoreEntityBuilder = FullEntity.newBuilder().setKey((IncompleteKey) key.getKey());

            for (Map.Entry<String, Object> prop: entityProperties.entrySet()) {
                String propName = prop.getKey();
                Object value = prop.getValue();

                if (value == null) {
                    //skip.
                    continue;
                }

                if (value instanceof IncompleteKey || value instanceof EntityKey) {
                    continue;
                }

                Value val = toValue(value, indexedProperties.contains(propName));

                if (val != null) { //null pointer issues can happen if we persist null.
                    if (!(val instanceof EntityValue) || !((EntityValue) val).get().getNames().isEmpty()) {
                        datastoreEntityBuilder.set(propName, val);
                    }
                }
            }

            FullEntity<IncompleteKey> partialEntity = datastoreEntityBuilder.build();
            FullEntity<Key> identifiedEntity = datastoreClient.put(partialEntity);

            EntityKey<Key> updatedKey = new SimpleEnitityKey(identifiedEntity.getKey(), Key.class);
            EntityUtils.updateEntityKey(entity, updatedKey);

            return entity;

        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException(e);
        }
    }

    private KeyFactory getKeyFactory(Class entityClazz) {
        if (this.keyFactories.get(entityClazz) == null) {
            String tableName = EntityUtils.getTableName(entityClazz);
            this.keyFactories.put(entityClazz, this.datastoreClient.newKeyFactory().setKind(tableName));
        }
        return this.keyFactories.get(entityClazz);
    }


    private static Value toValue(Object raw, boolean shouldIndex) {
        ValueBuilder builder = null;
        if (raw instanceof String) {
            builder = StringValue.newBuilder((String) raw);

        } else if (raw instanceof Double) {
            builder = DoubleValue.newBuilder((Double) raw);
        } else if (raw instanceof Long) {
            builder = LongValue.newBuilder((Long) raw);
        } else if (raw instanceof Short) {
            builder = LongValue.newBuilder(new Long((Short) raw));
        } else if (raw instanceof Integer) {
            builder = LongValue.newBuilder(new Long((Integer) raw));
        } else if (raw instanceof Boolean) {
            builder = BooleanValue.newBuilder((Boolean) raw);
        } else if (raw instanceof Set) {
            ListValue.Builder listValueBuilder = ListValue.newBuilder();

            for (Object inner: (Set) raw) {
                Value innerVal = toValue(inner, shouldIndex);
                if (innerVal != null) {
                    listValueBuilder.addValue(innerVal);
                } else {
                    //no support for a list of objects.  These should be different entities.
                    LOG.warn("No support for storing a set of Objects.  Please make these objects into first class entities: child type: {}", inner.getClass().getName());
                }
            }
            builder = listValueBuilder;
        } else {
            //attempt to pull apart the object.
            Map<String, Object> subEntity = EntityUtils.fetchAllProperties(raw);
            FullEntity.Builder innerBuilder = FullEntity.newBuilder();
            for (Map.Entry<String, Object> subEntProp: subEntity.entrySet()) {
                innerBuilder.set(subEntProp.getKey(), toValue(subEntProp.getValue(), false));
            }
            innerBuilder.set(TYPE_ATTRIBUTE_NAME, raw.getClass().getName());
            return EntityValue.newBuilder(innerBuilder.build()).setExcludeFromIndexes(true).build();

        }


        builder.setExcludeFromIndexes(!shouldIndex);


        return builder.build();

    }

    private static Object fromValue(Value value) {
        if (value instanceof StringValue) {
            return ((StringValue) value).get();
        } else if (value instanceof DoubleValue) {
            return ((DoubleValue) value).get();
        } else if (value instanceof LongValue) {
            return ((LongValue) value).get();
        } else if (value instanceof BooleanValue) {
            return ((BooleanValue) value).get();
        } else if (value instanceof ListValue) {
            ListValue.Builder listValueBuilder = ListValue.newBuilder();
            ArrayList<Object> values = new ArrayList<>(listValueBuilder.get().size());
            for (Value inner: listValueBuilder.get()) {
                Object innerVal = fromValue(inner);
                if (innerVal != null) {
                    values.add(innerVal);
                }
            }
            return values;
        } else if (value instanceof EntityValue) {
            FullEntity inner = ((EntityValue) value).get();
            Map<String, Object> innerProps = new HashMap<>();

            String innerType = inner.getString(TYPE_ATTRIBUTE_NAME);
            if (innerType == null || innerType.isEmpty()) {
                throw new RepositoryEntityException(String.format("Encountered A SubType that doesn't declare a type.  Cannot rehyrade!!  Inner Entity: %s", ((EntityValue) value).get().toString()));
            }
            try {
                Class innerTypeClass = GCPDatastoreDatasourceAdaptor.class.forName(innerType);

                for (Object dbPropName: inner.getNames()) {
                    String prop = ((String) dbPropName);
                    if (!prop.equals(TYPE_ATTRIBUTE_NAME)) {
                        Value innerVal = inner.getValue(prop);
                        innerProps.put(prop, fromValue(innerVal));
                    }
                }

                return EntityUtils.rehydrateObject(innerProps, innerTypeClass);

            } catch (ClassNotFoundException e) {
                throw new RepositoryEntityException(String.format("Encountered an invalid subtype.  Cannot rehyrade!!  Inner Entity : %s", ((EntityValue) value).get().toString()), e);
            }

        } else {
            return null; //no support.
        }
    }


    @Override
    public <ENTITY> void delete(EntityKey id, Class<ENTITY> clazz) {
        if (id.getKey() == null || !(id.getKey() instanceof Key)) {
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }
        Key entKey = (Key) id.getKey();

        try {
            datastoreClient.delete(entKey);
        } catch (Exception e) {
            throw new RepositoryInteractionException(e);
        }

    }

    @Override
    public <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz) {
        EntityKey<?> entityKey = null;
        try {
            entityKey = EntityUtils.findEntityKey(entity);
        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException(e);
        }
        this.delete(entityKey, clazz);
    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        if (id.getKey() == null || !(id.getKey() instanceof Key)) {
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }

        Key entKey = (Key) id.getKey();

        Entity entity = datastoreClient.get(entKey);


        Object result = null;
        try {
            result = toFromDBEntity(clazz, entity);
        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException("Unable to set entity key: Maybe the entity is missing the setter method for its key field?", e);
        }

        return (ENTITY) result;
    }

    private <ENTITY> ENTITY toFromDBEntity(Class<ENTITY> clazz, Entity entity) throws NoEntityKeyException {
        Set<String> propNamesInDB = entity.getNames();
        Map<String, Class> entityProperties = EntityUtils.getPropertyTypeMap(clazz);

        HashMap<String, Object> attributes = new HashMap<>();
        for (Map.Entry<String, Class> prop: entityProperties.entrySet()) {
            if (propNamesInDB.contains(prop.getKey())) {
                Value value = entity.getValue(prop.getKey());

                attributes.put(prop.getKey(), fromValue(value));
            }
        }


        ENTITY ent = (ENTITY) EntityUtils.rehydrateObject(attributes, clazz);

        EntityUtils.updateEntityKey(ent, new SimpleEnitityKey(entity.getKey(), Key.class));

        return ent;
    }


    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query, Class<ENTITY> clazz) {

        EntityQuery dbQuery = this.queryGenerator.toGQL(query, EntityUtils.getTableName(clazz));

        List<ENTITY> results = new ArrayList<>();
        QueryResults dbQueryResult = datastoreClient.run(dbQuery);

        while (dbQueryResult.hasNext()) {
            try {
                results.add(toFromDBEntity(clazz, (Entity) dbQueryResult.next()));
            } catch (NoEntityKeyException e) {
                throw new RepositoryEntityException("Unable to set entity key: Maybe the entity is missing the setter method for its key field?", e);
            }
        }

        QueryResult<ENTITY> queryResult = new QueryResult<>();
        queryResult.setNumResults(results.size());
        queryResult.setStartIndex(query.getPage().getStartIndex());
        queryResult.setResults(results);
        return queryResult;

    }


    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        EntityQuery q = Query.newEntityQueryBuilder().setKind(EntityUtils.getTableName(clazz)).build();

        List<ENTITY> results = new ArrayList<>();
        QueryResults dbQueryResult = datastoreClient.run(q);

        while (dbQueryResult.hasNext()) {
            try {
                results.add(toFromDBEntity(clazz, (Entity) dbQueryResult.next()));
            } catch (NoEntityKeyException e) {
                throw new RepositoryEntityException("Unable to set entity key: Maybe the entity is missing the setter method for its key field?", e);
            }
        }

        QueryResult<ENTITY> queryResult = new QueryResult<>();
        queryResult.setNumResults(results.size());
        queryResult.setStartIndex(0);
        queryResult.setResults(results);
        return queryResult;
    }

}
