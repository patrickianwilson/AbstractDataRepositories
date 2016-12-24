package com.patrickwilson.ardm.datasource.gcp.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.ValueBuilder;
import com.google.cloud.datastore.ValueType;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryInteractionException;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;
import com.patrickwilson.ardm.datasource.common.EntityUtils;
import com.patrickwilson.ardm.datasource.common.NoEntityKeyException;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptor implements QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {

    private Datastore datastoreClient;
    public static final Logger LOG = LoggerFactory.getLogger(GCPDatastoreDatasourceAdaptor.class);

    private Map<Class, KeyFactory> keyFactories = new HashMap<>();


    public static final int MAX_STRING_LENGTH = 500;

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

                datastoreEntityBuilder.set(propName, val);
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
            return null; //no support.
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
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        if (id.getKey() == null || !(id.getKey() instanceof Key)) {
            throw new RepositoryEntityException(String.format("Invalid Key class for Datastore.  Only %s is valid.", Key.class.getName()));
        }

        Key entKey = (Key) id.getKey();

        Entity entity = datastoreClient.get(entKey);

        Set<String> propNamesInDB = entity.getNames();
        Map<String, Class> entityProperties = EntityUtils.getPropertyTypeMap(clazz);

        HashMap<String, Object> attributes = new HashMap<>();
        for (Map.Entry<String, Class> prop: entityProperties.entrySet()) {
            if (propNamesInDB.contains(prop.getKey())) {
                Value value = entity.getValue(prop.getKey());

                attributes.put(prop.getKey(), fromValue(value));
            }
        }

        Object result = EntityUtils.rehydrateObject(attributes, clazz);

        return (ENTITY) result;
    }



    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query, Class<ENTITY> clazz) {
//        HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> indexes = selectTableIndexes(clazz);
//        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
//
//        HashSet<ENTITY> potentials; //sorted
//
//        int limit = Integer.MAX_VALUE;
//        int offset = 0;
//
//        if (query.getPage() != null && query.getPage() != null) {
//            if (query.getPage().getNumberOfResults() != QueryPage.NOT_SET) {
//                limit = query.getPage().getNumberOfResults();
//            }
//            offset = query.getPage().getStartIndex();
//        }
//
//        QueryResult<ENTITY> result = new QueryResult<>();
//
//        if (!query.getCriteria().getRootCriteria().hasSubNodes() && query.getCriteria().getRootCriteria() instanceof ValueEqualsLogicTreeNode) {
//            //try to use an index directly.
//            //compound indexes not supported.
//            //TODO - in the future we could probably support multiple OR clauses (by simply unioning the results).
//            ValueEqualsLogicTreeNode valuer = (ValueEqualsLogicTreeNode) query.getCriteria().getRootCriteria();
//
//            String indexName = valuer.getColumnName();
//            if (indexes.containsKey(indexName)) {
//                potentials = indexScan(indexName, query.getParameters()[valuer.getValueArgIndex()], clazz, offset, limit);
//            } else {
//                potentials = fullTableScan(query, clazz, offset, limit);
//            }
//
//        } else {
//            //drop to a full "table" scan + filter
//            potentials = fullTableScan(query, clazz, offset, limit);
//        }
//
//
//        result.setNumResults(potentials.size()); //may not be exactly what was asked for (but shouldn't be more).
//        result.setResults(Lists.newArrayList(potentials));
//        result.setStartIndex(offset);
//
//        return result;

        return null;

    }

    private <ENTITY> HashSet<ENTITY> fullTableScan(QueryData query, Class<ENTITY> clazz, int offset, int limit) {
//        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
//        boolean verboseLogging = true;
//        LOG.debug("No indexes can satisfy this query, resulting in Full Table Scan.  Query={}", query.getCriteria().toString());
//        int capacityRemaining = limit;
//        int discardsRemaining = offset;
//
//        HashSet<ENTITY> potentials = new HashSet<>();
//        for (Object o: mockDb.values()) {
//            //shortcut.
//            if (capacityRemaining <= 0) {
//                break; //no point in continuing.
//            }
//
//            ENTITY entity = (ENTITY) o;
//            if (query.getCriteria() != null && query.getCriteria().getRootCriteria() != null) {
//                if (matches(EntityUtils.fetchIndexableProperties(entity), query.getCriteria().getRootCriteria(), query, verboseLogging)) {
//                    if (capacityRemaining > 0 && discardsRemaining <= 0) {
//                        potentials.add(entity);
//                        capacityRemaining--;
//                        discardsRemaining--;
//                    }
//                }
//            } else {
//                //no filter - just add all.
//                if (capacityRemaining > 0 && discardsRemaining <= 0) {
//                    potentials.add(entity);
//                    capacityRemaining--;
//                }
//            }
//            verboseLogging = false; //only the first entity needs logs otherwise it just turns to spamming.
//        }
//
//        return potentials;

        return null;
    }

    private <ENTITY> HashSet<ENTITY> indexScan(String index, Object queryValue, Class<ENTITY> clazz, int offset, int limit) {
//        HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> indexes = selectTableIndexes(clazz);
//        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
//
//        boolean verboseLogging = true;
//        LOG.debug("Query Plan:  using index {} and searching for value '{}'", index, queryValue);
//        int capacityRemaining = limit;
//        int discardsRemaining = offset;
//
//        HashSet<ENTITY> potentials = new HashSet<>();
//
//        for (Object key: indexes.get(index).get(queryValue)) {
//            //shortcut.
//            if (capacityRemaining <= 0) {
//                break; //no point in continuing.
//            }
//
//            //this is not really very efficient -> a scatter/gather type operation.
//            ENTITY entity = (ENTITY) mockDb.get(key);
//
//            if (capacityRemaining > 0 && discardsRemaining <= 0) {
//                potentials.add(entity);
//                capacityRemaining--;
//            }
//
//            verboseLogging = false; //only the first entity needs logs otherwise it just turns to spamming.
//        }
//
//        return potentials;
        return null;
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
//        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
//
//        QueryResult<ENTITY> result = new QueryResult<>();
//        List<ENTITY> holder = new ArrayList<>();
//        for (Object ent: mockDb.values()) {
//            holder.add((ENTITY) ent);
//        }
//        result.setNumResults(holder.size());
//        result.setResults(holder);
//        result.setStartIndex(0);
//        return result;

        return null;
    }

    /**
     * clear the sample database.
     */
//    public void clearDatabase() {
//        this.tables.clear();
//    }
//
//    private <ENTITY> void createTable(Class<ENTITY> entityType) throws RepositoryEntityException {
//        try {
//            this.tables.put(entityType, new TreeMap<>(new GenericKeyComparator(entityType)));
//            this.tableIndexSelector.put(entityType, new HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>>());
//        } catch (NoEntityKeyException e) {
//            throw new RepositoryEntityException(entityType, e);
//        }
//    }
//
//    private TreeMap<EntityKey<Comparable>, Object> selectTable(Class entityType) {
//        if (!tables.containsKey(entityType)) {
//            throw new NoSuchEntityRepositoryException(entityType);
//        }
//
//        return this.tables.get(entityType);
//    }
//
//    private HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> selectTableIndexes(Class entityType) {
//        if (!tables.containsKey(entityType)) {
//            throw new NoSuchEntityRepositoryException(entityType);
//        }
//
//        return this.tableIndexSelector.get(entityType);
//
//    }
//}
}
