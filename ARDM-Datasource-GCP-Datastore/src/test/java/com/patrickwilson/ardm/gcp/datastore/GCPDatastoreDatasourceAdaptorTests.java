package com.patrickwilson.ardm.gcp.datastore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.IncompleteKey;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.ardm.datasource.api.query.LogicTreeCompositeNode;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryLogicTree;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.ValueEqualsLogicTreeNode;
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptorTests {


    public static final short SAMPLE_AGE = 35;
    public static final int EVENTUAL_CONSISTENCY_PAUSE = 2000;
    private static Datastore datastore;

    private static GCPDatastoreDatasourceAdaptor underTest = null;
    private static SimpleEntity result = null;
    private static SimpleEntity secondResult = null;
    private static ChildEntity child1Persisted  = null;

    @BeforeClass
    public static void startupSuite() throws IOException, InterruptedException {

        InputStream credStream = null;

        //try to locate a file based on a env variable
        String credFile = System.getProperty("GoogleServiceAccountCredentialFile");
        if (credFile != null) {
            credStream = new FileInputStream(credFile);
            System.out.println(String.format("Loading GCloud Credentials From: %s, Successful = %s", credFile, credStream != null));
        }

        if (credStream != null) {
            datastore = DatastoreOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(credStream))
                    .build()
                    .getService();
        } else {
            System.out.println("Using Default gcloud Credentials for Datastore.");
            datastore = DatastoreOptions.newBuilder().build().getService();
        }

        underTest = new GCPDatastoreDatasourceAdaptor(datastore);
        SimpleEntity entity = new SimpleEntity();
        entity.setEmail("patrick.ian.wilson@gmail.com");
        entity.setFirstName("Patrick");
        entity.setAge(SAMPLE_AGE);
        entity.setInner(new InnerObject()); //this should not be persisted at all.

        SimpleEntity second = new SimpleEntity();
        second.setEmail("patrick.andrew.wilson@gmail.com");
        second.setFirstName("Patrick");
        second.setAge(SAMPLE_AGE);
        second.setInner(new InnerObject());
        second.getInner().setInnerField("some string test");
        second.getInner().setInner(new InnerInnerObject());
        second.getInner().getInner().setNum(-1);



        result = underTest.save(entity, SimpleEntity.class);
        secondResult = underTest.save(second, SimpleEntity.class);

        ChildEntity child1 = new ChildEntity();
        com.google.cloud.datastore.Key parent = (com.google.cloud.datastore.Key) result.entityKey;
        child1.setKey(underTest.<ChildEntity, IncompleteKey>buildPrefixKey(parent, ChildEntity.class));
        child1.setNow(new Date());

         child1Persisted = underTest.save(child1, ChildEntity.class);

        Thread.sleep(EVENTUAL_CONSISTENCY_PAUSE); //ensure datastore consistency.
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        underTest.delete(new SimpleEnitityKey(result.getEntityKey(), com.google.cloud.datastore.Key.class), SimpleEntity.class);
        underTest.delete(new SimpleEnitityKey(secondResult.getEntityKey(), com.google.cloud.datastore.Key.class), SimpleEntity.class);
        underTest.delete(new SimpleEnitityKey(child1Persisted.getKey(), com.google.cloud.datastore.Key.class), SimpleEntity.class);
        Thread.sleep(EVENTUAL_CONSISTENCY_PAUSE); //ensure datastore consistency.
    }

    @Test
    public void doIt() throws InterruptedException {

        com.google.cloud.datastore.Key key = ((com.google.cloud.datastore.Key) result.getEntityKey());
        com.google.cloud.datastore.Key secondKey = ((com.google.cloud.datastore.Key) secondResult.getEntityKey());

        Assert.assertNotNull(key.getId());

        SimpleEntity fromDB = underTest.findOne(new SimpleEnitityKey(key, com.google.cloud.datastore.Key.class), SimpleEntity.class);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(result.email, fromDB.email);
        Assert.assertEquals(result.firstName, fromDB.firstName);
        Assert.assertEquals(result.age, fromDB.age);

        SimpleEntity secondFromDB = underTest.findOne(new SimpleEnitityKey(secondKey, com.google.cloud.datastore.Key.class), SimpleEntity.class);

        Assert.assertNotNull(secondFromDB);
        Assert.assertEquals(secondResult.email, secondFromDB.email);
        Assert.assertEquals(secondResult.firstName, secondFromDB.firstName);
        Assert.assertEquals(secondResult.age, secondFromDB.age);

        //https://github.com/patrickianwilson/AbstractDataRepositories/issues/9
        Assert.assertNotNull(secondFromDB.entityKey);

        //https://github.com/patrickianwilson/AbstractDataRepositories/issues/10
        Assert.assertNotNull(secondFromDB.inner);
        Assert.assertEquals("some string test", secondFromDB.inner.innerField);
        Assert.assertNotNull(secondFromDB.inner.inner);
        Assert.assertEquals(-1, secondFromDB.inner.inner.num);

        QueryResult<SimpleEntity> allEntities = underTest.findAll(SimpleEntity.class);

        Assert.assertNotNull(allEntities);
        Assert.assertEquals(2, allEntities.getNumResults());
        Assert.assertNotNull(allEntities.getResults());
        Assert.assertEquals(2, allEntities.getResults().size());


    }

    @Test
    public void shouldFindEntitiesViaQuery() {

        QueryLogicTree firstNameCriteria = new QueryLogicTree(new ValueEqualsLogicTreeNode("firstName", 0));

        QueryData query = new QueryData(new QueryPage(0, -1), firstNameCriteria);
        query.setParameters(new Object[] {"Patrick"});

        QueryResult<SimpleEntity> qResult = underTest.findByCriteria(query, SimpleEntity.class);

        Assert.assertNotNull(qResult);
        Assert.assertEquals(2, qResult.getNumResults());
        Assert.assertNotNull(qResult.getResults());
        Assert.assertEquals(2, qResult.getResults().size());
    }

    @Test
    public void shouldFindEntitiesViaPrefixScan() {
        QueryResult<ChildEntity> qResult = underTest.findAllWithKeyPrefix(result.entityKey, ChildEntity.class);
        Assert.assertNotNull(qResult);
        Assert.assertEquals(1, qResult.getNumResults());
        Assert.assertNotNull(qResult.getResults().get(0));
        Assert.assertTrue(qResult.getResults().get(0).getKey() instanceof com.google.cloud.datastore.Key);
        Assert.assertNotNull(((com.google.cloud.datastore.Key) qResult.getResults().get(0).getKey()).getId());

    }


    @Test
    public void shouldFindEntitiesViaConjunctionQuery() {
        LogicTreeCompositeNode root  = new LogicTreeCompositeNode();
        root.addSubNode(new ValueEqualsLogicTreeNode("firstName", 0));
        root.addSubNode(new ValueEqualsLogicTreeNode("email", 1));
        root.setConjection(LogicTreeCompositeNode.Conjection.AND);

        QueryLogicTree firstNameCriteria = new QueryLogicTree(root);

        QueryData query = new QueryData(new QueryPage(0, -1), firstNameCriteria);
        query.setParameters(new Object[] {"Patrick", "patrick.ian.wilson@gmail.com"});

        QueryResult<SimpleEntity> qResult = underTest.findByCriteria(query, SimpleEntity.class);

        Assert.assertNotNull(qResult);
        Assert.assertEquals(1, qResult.getNumResults());
        Assert.assertNotNull(qResult.getResults());
        Assert.assertEquals(1, qResult.getResults().size());
    }


    /**
     * For testing.
     */
    @Entity(domainOrTable = "SimpleEntityForTesting")
    public static class SimpleEntity {

        private com.google.cloud.datastore.IncompleteKey entityKey;
        private String firstName;
        private String email;
        private short age;
        private InnerObject inner;

        public com.google.cloud.datastore.IncompleteKey getEntityKey() {
            return entityKey;
        }

        @Key(keyClass = IncompleteKey.class)
        public void setEntityKey(com.google.cloud.datastore.IncompleteKey entityKey) {
            this.entityKey = entityKey;
        }

        public String getFirstName() {
            return firstName;
        }

        @Indexed
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getEmail() {
            return email;
        }

        @Indexed
        public void setEmail(String email) {
            this.email = email;
        }

        public Short getAge() {
            return age;
        }

        public void setAge(Short age) {
            this.age = age;
        }

        public InnerObject getInner() {
            return inner;
        }

        public void setInner(InnerObject inner) {
            this.inner = inner;
        }
    }


    /**
     * Test nested objects.
     */
    public static class InnerObject {
        private String innerField;
        private InnerInnerObject inner;

        public String getInnerField() {
            return innerField;
        }

        public void setInnerField(String innerField) {
            this.innerField = innerField;
        }

        public InnerInnerObject getInner() {
            return inner;
        }

        public void setInner(InnerInnerObject inner) {
            this.inner = inner;
        }
    }

    /**
     * for testing.
     */
    public static class InnerInnerObject {
        private int num;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }


    /**
     * For testing.
     */
    @Entity
    public static class ChildEntity {
        private IncompleteKey key;
        private Date now;

        public IncompleteKey getKey() {
            return key;
        }

        @Key(keyClass = com.google.cloud.datastore.IncompleteKey.class)
        public void setKey(IncompleteKey key) {
            this.key = key;
        }

        public Date getNow() {
            return now;
        }

        public void setNow(Date now) {
            this.now = now;
        }
    }

}
