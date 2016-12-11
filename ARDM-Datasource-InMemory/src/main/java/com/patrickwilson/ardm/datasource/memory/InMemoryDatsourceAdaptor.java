package com.patrickwilson.ardm.datasource.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.NoSuchEntityRepositoryException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryInteractionException;
import com.patrickwilson.ardm.datasource.api.query.LogicTreeCompositeNode;
import com.patrickwilson.ardm.datasource.api.query.LogicTreeNode;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;
import com.patrickwilson.ardm.datasource.api.query.ValueEqualsLogicTreeNode;
import com.patrickwilson.ardm.datasource.common.EntityUtils;
import com.patrickwilson.ardm.datasource.common.NoEntityKeyException;

/**
 * The in memory datasource is designed to be both a reference implementation and a
 * light weight emulator for fast and precise local development.
 *
 */
public class InMemoryDatsourceAdaptor implements QueriableDatasourceAdaptor, CRUDDatasourceAdaptor, ScanableDatasourceAdaptor {
    public static final Logger LOG = LoggerFactory.getLogger(InMemoryDatsourceAdaptor.class);

    //the next two datastructures are the basis of the inmemory implementation.
    private HashMap<Class, TreeMap<EntityKey<Comparable>, Object>> tables = new HashMap<>();  //horrendous but necessary to represent "tables"
    private HashMap<Class, HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>>> tableIndexSelector = new HashMap<>();

    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {
        if (!tables.containsKey(clazz)) {
            createTable(clazz);

            //this table doesn't yet exist.
        }
        TreeMap<EntityKey<Comparable>, Object> mockDb = tables.get(clazz);
        try {
            EntityKey key = EntityUtils.findEntityKey(entity);

            if (key == null) {
                //means the key was of the type "EntityKey" but we don't know how to auto generate keys..\
                throw new RepositoryEntityException("Only String key types are supported.");
            }
            if (key.getKey() == null) {
                if ((!key.getKeyClass().equals(String.class)) && (!key.getKeyClass().equals(Object.class))) {
                    throw new RepositoryEntityException("Only String or Comparable key types are supported.");
                }
                String newKey = UUID.randomUUID().toString();
                key = new SimpleEnitityKey(newKey, String.class);
                EntityUtils.updateEntityKey(entity, key);
            }
            mockDb.put(key, entity);

            updateIndexes(entity, clazz);
            return entity;

        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException(e);
        }
    }

    /**
     * automatically add indexes for each property.
     * @param entity
     */
    private void updateIndexes(Object entity, Class entityType) throws NoEntityKeyException {
        HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> indexes = selectTableIndexes(entityType);
        Map<String, Object> indexedProps = EntityUtils.FetchIndexableProperties(entity);

        for (String index: indexedProps.keySet()) {
            if (!indexes.containsKey(index)) {
                indexes.put(index, new TreeMap<Object, List<EntityKey<Comparable>>>()); //create a new index
            }

            if (!indexes.get(index).containsKey(indexedProps.get(index))) {
                indexes.get(index).put(indexedProps.get(index), new LinkedList<EntityKey<Comparable>>());
            }

            indexes.get(index).get(indexedProps.get(index)).add(EntityUtils.findEntityKey(entity)); //reverse index (value -> object.key)
         }

    }

    @Override
    public <ENTITY> void delete(EntityKey id, Class<ENTITY> clazz) {
        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
        mockDb.remove(id);

    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
        return (ENTITY) mockDb.get(id);
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findByCriteria(QueryData query, Class<ENTITY> clazz) {
        HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> indexes = selectTableIndexes(clazz);
        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);

        HashSet<ENTITY> potentials; //sorted

        int limit = Integer.MAX_VALUE;
        int offset = 0;

        if (query.getPage() != null && query.getPage() != null) {
            if (query.getPage().getNumberOfResults() != QueryPage.NOT_SET) {
                limit = query.getPage().getNumberOfResults();
            }
            offset = query.getPage().getStartIndex();
        }

        QueryResult<ENTITY> result = new QueryResult<>();

        if (!query.getCriteria().getRootCriteria().hasSubNodes() && query.getCriteria().getRootCriteria() instanceof ValueEqualsLogicTreeNode) {
            //try to use an index directly.
            //compound indexes not supported.
            //TODO - in the future we could probably support multiple OR clauses (by simply unioning the results).
            ValueEqualsLogicTreeNode valuer = (ValueEqualsLogicTreeNode) query.getCriteria().getRootCriteria();

            String indexName = valuer.getColumnName();
            if (indexes.containsKey(indexName)) {
                potentials = indexScan(indexName, query.getParameters()[valuer.getValueArgIndex()], clazz, offset, limit);
            } else {
                potentials = fullTableScan(query, clazz, offset, limit);
            }

        } else {
            //drop to a full "table" scan + filter
            potentials = fullTableScan(query, clazz, offset, limit);
        }


        result.setNumResults(potentials.size()); //may not be exactly what was asked for (but shouldn't be more).
        result.setResults(Lists.newArrayList(potentials));
        result.setStartIndex(offset);

        return result;

    }

    private <ENTITY> HashSet<ENTITY> fullTableScan(QueryData query, Class<ENTITY> clazz, int offset, int limit) {
        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);
        boolean verboseLogging = true;
        LOG.debug("No indexes can satisfy this query, resulting in Full Table Scan.  Query={}", query.getCriteria().toString());
        int capacityRemaining = limit;
        int discardsRemaining = offset;

        HashSet<ENTITY> potentials = new HashSet<>();
        for (Object o: mockDb.values()) {
            //shortcut.
            if (capacityRemaining <= 0) {
                break; //no point in continuing.
            }

            ENTITY entity = (ENTITY)o;
            if (query.getCriteria() != null && query.getCriteria().getRootCriteria() != null) {
                if(matches(EntityUtils.FetchIndexableProperties(entity), query.getCriteria().getRootCriteria(), query, verboseLogging)) {
                    if (capacityRemaining > 0 && discardsRemaining <= 0) {
                        potentials.add(entity);
                        capacityRemaining--;
                        discardsRemaining--;
                    }
                }
            } else {
                //no filter - just add all.
                if (capacityRemaining > 0 && discardsRemaining <= 0) {
                    potentials.add(entity);
                    capacityRemaining--;
                }
            }
            verboseLogging = verboseLogging && false; //only the first entity needs logs otherwise it just turns to spamming.
        }

        return potentials;
    }

    private <ENTITY> HashSet<ENTITY> indexScan(String index, Object queryValue, Class<ENTITY> clazz, int offset, int limit) {
        HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> indexes = selectTableIndexes(clazz);
        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);

        boolean verboseLogging = true;
        LOG.debug("Query Plan:  using index {} and searching for value '{}'", index, queryValue);
        int capacityRemaining = limit;
        int discardsRemaining = offset;

        HashSet<ENTITY> potentials = new HashSet<>();

        for (Object key: indexes.get(index).get(queryValue)) {
            //shortcut.
            if (capacityRemaining <= 0) {
                break; //no point in continuing.
            }

            //this is not really very efficient -> a scatter/gather type operation.
            ENTITY entity = (ENTITY)mockDb.get(key);

            if (capacityRemaining > 0 && discardsRemaining <= 0) {
                    potentials.add(entity);
                    capacityRemaining--;
            }

            verboseLogging = verboseLogging && false; //only the first entity needs logs otherwise it just turns to spamming.
        }

        return potentials;
    }


    private <ENTITY> boolean matches(Map<String, Object> indexedProps, LogicTreeNode node, QueryData query, boolean verboseLogging) {
        if (!node.hasSubNodes()) {
            if (node instanceof ValueEqualsLogicTreeNode) {
                // use .equals to determine equality.
                ValueEqualsLogicTreeNode clause = (ValueEqualsLogicTreeNode) node;
                //TODO this line is not very defensive...  assuming the param map in the query object is already verified so we don't go out of bounds...
                if (!indexedProps.containsKey(clause.getColumnName()) && verboseLogging) {
                    LOG.warn("Attempting to query against un-indexed field!  Many data sources will simply omit this result..  Dropping entity from result set.");
                }
                return indexedProps.containsKey(clause.getColumnName()) && indexedProps.get(clause.getColumnName()).equals(query.getParameters()[clause.getValueArgIndex()]);

            } else {
                throw new RepositoryInteractionException(String.format("Missing definition for handling query clause type: %s", node.getClass().getName()));
            }
        } else if (node instanceof LogicTreeCompositeNode) {
            LogicTreeCompositeNode conjunctionClause = (LogicTreeCompositeNode) node;
            if (conjunctionClause.getConjection() == LogicTreeCompositeNode.Conjection.OR) {
                boolean result = false; //cant start true or the OR clause would always be true
                for (LogicTreeNode child: conjunctionClause.getSubNodes()) {
                    result = result || matches(indexedProps, child, query, verboseLogging);
                }
                return result;
            } else if (conjunctionClause.getConjection() == LogicTreeCompositeNode.Conjection.AND) {
                boolean result = true; //can start false or the AND clause would always be false.
                for (LogicTreeNode child: conjunctionClause.getSubNodes()) {
                    result = result && matches(indexedProps, child, query, verboseLogging);
                }
                return result;
            } else {
                throw new RepositoryInteractionException(String.format("Unrecognized conjunction type: %s", conjunctionClause.getConjection().name()));
            }

        } else {
            throw new RepositoryInteractionException(String.format("Invalid Query Tree, unknown logic node type: %s", node.getClass().getName()));
        }
    }

    @Override
    public <ENTITY> QueryResult<ENTITY> findAll(Class<ENTITY> clazz) {
        TreeMap<EntityKey<Comparable>, Object> mockDb = selectTable(clazz);

        QueryResult<ENTITY> result = new QueryResult<>();
        List<ENTITY> holder = new ArrayList<>();
        for (Object ent: mockDb.values()) {
            holder.add((ENTITY) ent);
        }
        result.setNumResults(holder.size());
        result.setResults(holder);
        result.setStartIndex(0);
        return result;
    }

    /**
     * clear the sample database.
     */
    public void clearDatabase() {
        this.tables.clear();
    }

    private <ENTITY> void createTable(Class<ENTITY> entityType) throws RepositoryEntityException {
        try {
            this.tables.put(entityType, new TreeMap<>(new GenericKeyComparator(entityType)));
            this.tableIndexSelector.put(entityType, new HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>>());
        } catch (NoEntityKeyException e) {
            throw new RepositoryEntityException(entityType, e);
        }
    }

    private TreeMap<EntityKey<Comparable>, Object> selectTable(Class entityType) {
        if (!tables.containsKey(entityType)) {
            throw new NoSuchEntityRepositoryException(entityType);
        }

        return this.tables.get(entityType);
    }

    private HashMap<String, TreeMap<Object, List<EntityKey<Comparable>>>> selectTableIndexes(Class entityType) {
        if (!tables.containsKey(entityType)) {
            throw new NoSuchEntityRepositoryException(entityType);
        }

        return this.tableIndexSelector.get(entityType);

    }
}
