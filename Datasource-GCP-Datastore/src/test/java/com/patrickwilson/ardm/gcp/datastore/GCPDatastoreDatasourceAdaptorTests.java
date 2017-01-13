package com.patrickwilson.ardm.gcp.datastore;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.IncompleteKey;
import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.LinkedKey;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.ardm.datasource.api.query.LogicTreeCompositeNode;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryLogicTree;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.ValueEqualsLogicTreeNode;
import com.patrickwilson.ardm.datasource.gcp.datastore.DatastoreEntityKey;
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptorTests {


    public static final short SAMPLE_AGE = 35;
    public static final int EVENTUAL_CONSISTENCY_PAUSE = 2000;
    public static final long SAMPLE_ID = 100L;
    public static final long DOES_NOT_EXIST = 102391237309821L;
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

        second.setEmbeddedObjects(Lists.newArrayList(new InnerObject("inner1"), new InnerObject("inner2")));



        result = underTest.save(entity, SimpleEntity.class);
        secondResult = underTest.save(second, SimpleEntity.class);

        ChildEntity child1 = new ChildEntity();
        LinkedKey parent = result.entityKey;
        child1.setKey(underTest.buildPrefixKey(parent, ChildEntity.class));
        child1.setNow(new Date());

         child1Persisted = underTest.save(child1, ChildEntity.class);

        Thread.sleep(EVENTUAL_CONSISTENCY_PAUSE); //ensure datastore consistency.
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        underTest.delete(result.getEntityKey(), SimpleEntity.class);
        underTest.delete(secondResult.getEntityKey(), SimpleEntity.class);
        underTest.delete(child1Persisted.getKey(), SimpleEntity.class);
        Thread.sleep(EVENTUAL_CONSISTENCY_PAUSE); //ensure datastore consistency.
    }

    @Test
    public void doIt() throws InterruptedException {

        com.google.cloud.datastore.Key key = (com.google.cloud.datastore.Key) result.getEntityKey().getKey();
        com.google.cloud.datastore.Key secondKey = ((com.google.cloud.datastore.Key) secondResult.getEntityKey().getKey());

        Assert.assertNotNull(key.getId());

        SimpleEntity fromDB = underTest.findOne(new DatastoreEntityKey(key), SimpleEntity.class);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(result.email, fromDB.email);
        Assert.assertEquals(result.firstName, fromDB.firstName);
        Assert.assertEquals(result.age, fromDB.age);

        SimpleEntity secondFromDB = underTest.findOne(new DatastoreEntityKey(secondKey), SimpleEntity.class);

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

        Assert.assertNotNull(secondFromDB.getEmbeddedObjects());
        Assert.assertEquals(2, secondFromDB.getEmbeddedObjects().size());
        Assert.assertEquals("inner1", secondFromDB.getEmbeddedObjects().get(0).getInnerField());

        QueryResult<SimpleEntity> allEntities = underTest.findAll(SimpleEntity.class);

        Assert.assertNotNull(allEntities);
        Assert.assertEquals(2, allEntities.getNumResults());
        Assert.assertNotNull(allEntities.getResults());
        Assert.assertEquals(2, allEntities.getResults().size());

        SimpleEntity shouldBeNull = underTest.findOne(underTest.buildKey(DOES_NOT_EXIST, SimpleEntity.class), SimpleEntity.class);
        Assert.assertNull(shouldBeNull);

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
        Assert.assertTrue(qResult.getResults().get(0).getKey().getKey() instanceof com.google.cloud.datastore.Key);
        Assert.assertNotNull(((com.google.cloud.datastore.Key) qResult.getResults().get(0).getKey().getKey()).getId());

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

    @Test
    public void shouldGenerateValidKey() {
        EntityKey k = underTest.<SimpleEntity>buildKey(SAMPLE_ID, SimpleEntity.class);
        Assert.assertNotNull(k);
        Assert.assertEquals(SAMPLE_ID, (long) ((com.google.cloud.datastore.Key) k.getKey()).getId());
        Assert.assertEquals("SimpleEntityForTesting", ((com.google.cloud.datastore.Key) k.getKey()).getKind());

        k = underTest.<SimpleEntity>buildKey("abc123", SimpleEntity.class);
        Assert.assertNotNull(k);
        Assert.assertEquals("abc123", ((com.google.cloud.datastore.Key) k.getKey()).getName());
        Assert.assertEquals("SimpleEntityForTesting", ((com.google.cloud.datastore.Key) k.getKey()).getKind());

    }


    /**
     * For testing.
     */
    @Entity(domainOrTable = "SimpleEntityForTesting")
    public static class SimpleEntity {

        private LinkedKey entityKey;
        private String firstName;
        private String email;
        private short age;
        private InnerObject inner;
        private List<InnerObject> embeddedObjects = new ArrayList<>();

        @Key(IncompleteKey.class)
        public void setEntityKey(LinkedKey entityKey) {
            this.entityKey = entityKey;
        }

        public LinkedKey getEntityKey() {
            return entityKey;
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

        public List<InnerObject> getEmbeddedObjects() {
            return embeddedObjects;
        }

        public void setEmbeddedObjects(List<InnerObject> embeddedObjects) {
            this.embeddedObjects = embeddedObjects;
        }
    }


    /**
     * Test nested objects.
     */
    public static class InnerObject {
        private String innerField;
        private InnerInnerObject inner;

        public InnerObject(String innerField) {
            this.innerField = innerField;
        }

        public InnerObject() {
        }

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
        private LinkedKey key;
        private Date now;

        public LinkedKey getKey() {
            return key;
        }

        @Key(com.google.cloud.datastore.IncompleteKey.class)
        public void setKey(LinkedKey key) {
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
