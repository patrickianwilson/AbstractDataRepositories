package com.patrickwilson.ardm.datasource.gcp.datastore;
/*
 The MIT License (MIT)

 Copyright (c) 2014 Patrick Wilson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.cloud.datastore.*;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.LinkedKey;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryInteractionException;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.common.EntityUtils;
import com.patrickwilson.ardm.datasource.common.NoEntityKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            EntityKey key = EntityUtils.findEntityKeyType(entity);

            if (!IncompleteKey.class.isAssignableFrom(key.getKeyClass())) {
                //means the key was of the type "EntityKey" but we don't know how to auto generate keys..\
                throw new RepositoryEntityException("Only Datastore Key types are supported.");
            }

            if (key.getKey() == null) {
                KeyFactory entityKeyFactory = getKeyFactory(clazz);
                IncompleteKey datastoreKey = entityKeyFactory.newKey();
                key = new DatastoreEntityKey(datastoreKey);
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

            DatastoreEntityKey updatedKey = new DatastoreEntityKey(identifiedEntity.getKey());

            EntityUtils.updateEntityKey(entity, updatedKey);

            return entity;

        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException(e);
        }
    }

    private KeyFactory getKeyFactory(Class entityClazz) {
        String tableName = EntityUtils.getTableName(entityClazz);
        if (this.keyFactories.get(entityClazz) == null) {
            this.keyFactories.put(entityClazz, this.datastoreClient.newKeyFactory().setKind(tableName));
        }
        //make sure we wipe out any previous ancestor...
        this.keyFactories.get(entityClazz).reset().setKind(tableName);
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
        } else if (raw instanceof Date) {
            builder = DateTimeValue.newBuilder(DateTime.copyFrom((Date) raw));
        } else if (raw instanceof Calendar) {
            builder = DateTimeValue.newBuilder(DateTime.copyFrom((Calendar) raw));
        } else if (raw instanceof List) {
            ListValue.Builder listValueBuilder = ListValue.newBuilder();

            for (Object inner: (List) raw) {
                Value innerVal = toValue(inner, shouldIndex);
                if (innerVal != null && !(innerVal instanceof ListValue)) {
                    listValueBuilder.addValue(innerVal);
                } else {
                    //no support for a list of objects.  These should be different entities.
                    LOG.warn("No support for storing a set of Objects.  Please make these objects into first class entities: child type: {}", inner.getClass().getName());
                }
            }

            return listValueBuilder.build(); //return right away.  List value types cannot be excluded from index.

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
        } else if (value instanceof DateTimeValue) {
            return ((DateTimeValue) value).get().toCalendar();
        } else if (value instanceof ListValue) {
            List<? extends Value> dbValues = ((ListValue) value).get();
            ArrayList<Object> values = new ArrayList<>(dbValues.size());
            for (Value inner: dbValues) {
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
        if (id == null || !id.isPopulated() || !Key.class.isAssignableFrom(id.getKey().getClass())) {
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
        EntityKey entityKey = null;
        try {
            entityKey = EntityUtils.findEntityKeyType(entity);
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

        if (entity == null) {
            return null;
        }

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

        EntityUtils.updateEntityKey(ent, new DatastoreEntityKey(entity.getKey()));

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


    @Override
    public <ENTITY> QueryResult<ENTITY> findAllWithKeyPrefix(EntityKey prefix, Class<ENTITY> clazz) {
        if (!Key.class.isAssignableFrom(prefix.getKeyClass())) {
            //in datastore a prefix query is called an ancestor query.  it must be a Key class.
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }
        EntityQuery q = Query.newEntityQueryBuilder()
                .setKind(EntityUtils.getTableName(clazz))
                .setFilter(StructuredQuery.PropertyFilter.hasAncestor((Key) prefix.getKey()))
                .build();

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

    @Override
    public <ENTITY> LinkedKey buildPrefixKey(EntityKey prefix, Class<ENTITY> clazz) {
        if (!Key.class.isAssignableFrom(prefix.getKeyClass())) {
            //in datastore a prefix query is called an ancestor query.  it must be a Key class.
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }

        Key parent = (Key) prefix.getKey();

        KeyFactory prefixFactory = getKeyFactory(clazz);
        if (parent.getName() != null)
            prefixFactory.addAncestor(PathElement.of(parent.getKind(), parent.getName()));
        else
            prefixFactory.addAncestor(PathElement.of(parent.getKind(), parent.getId()));

        return new DatastoreEntityKey(prefixFactory.newKey());
    }

    @Override
    public <ENTITY> EntityKey buildKey(String id, Class<ENTITY> clazz) {
        KeyFactory keys = getKeyFactory(clazz);
        return new DatastoreEntityKey(keys.newKey(id));
    }

    @Override
    public <ENTITY> EntityKey buildKey(long id, Class<ENTITY> clazz) {
        KeyFactory keys = getKeyFactory(clazz);
        return new DatastoreEntityKey(keys.newKey(id));
    }

    @Override
    public <ENTITY> EntityKey buildEmptyKey(Class<ENTITY> clazz) {
        KeyFactory keys = getKeyFactory(clazz);
        return new DatastoreEntityKey(keys.newKey());
    }

    @Override
    public <ENTITY> LinkedKey buildPrefixKey(EntityKey prefix, String id, Class<ENTITY> clazz) {
        if (!Key.class.isAssignableFrom(prefix.getKeyClass())) {
            //in datastore a prefix query is called an ancestor query.  it must be a Key class.
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }

        Key parent = (Key) prefix.getKey();

        KeyFactory prefixFactory = getKeyFactory(clazz);

        prefixFactory.addAncestor(getPathElement(parent));
        return new DatastoreEntityKey(prefixFactory.newKey(id));
    }

    @Override
    public <ENTITY> LinkedKey buildPrefixKey(EntityKey prefix, long id, Class<ENTITY> clazz) {
        if (!Key.class.isAssignableFrom(prefix.getKeyClass())) {
            //in datastore a prefix query is called an ancestor query.  it must be a Key class.
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }

        Key parent = (Key) prefix.getKey();

        KeyFactory prefixFactory = getKeyFactory(clazz);

        prefixFactory.addAncestor(getPathElement(parent));
        return new DatastoreEntityKey(prefixFactory.newKey(id));
    }

    private PathElement getPathElement(Key parent) {
        if (parent.hasId()) {
            return PathElement.of(parent.getKind(), parent.getId());
        } else if (parent.hasName()) {
            return PathElement.of(parent.getKind(), parent.getName());
        } else {
            throw new IllegalArgumentException("Cannot construct a LinkedKey for a IncompleteKey.  First save() the parent object and then use the updated key.");
        }
    }
}

