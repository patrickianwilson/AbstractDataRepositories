package com.patrickwilson.ardm.gcp.datastore;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.IncompleteKey;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.api.query.*;
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;
import org.junit.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptorTests {


    public static final short SAMPLE_AGE = 35;
    public static final int EVENTUAL_CONSISTENCY_PAUSE = 500;
    private static Datastore datastore;

    @BeforeClass
    public static void startupSuite() throws IOException {

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
            System.out.println("Using Application Default Credentials for Datastore.");
            datastore = DatastoreOptions.newBuilder().build().getService();
        }

    }

    private GCPDatastoreDatasourceAdaptor underTest = null;
    private SimpleEntity result = null;
    private SimpleEntity secondResult = null;

    @Before
    public void setup() throws InterruptedException {

        underTest = new GCPDatastoreDatasourceAdaptor(datastore);
        SimpleEntity entity = new SimpleEntity();
        entity.setEmail("patrick.ian.wilson@gmail.com");
        entity.setFirstName("Patrick");
        entity.setAge(SAMPLE_AGE);

        SimpleEntity second = new SimpleEntity();
        second.setEmail("patrick.andrew.wilson@gmail.com");
        second.setFirstName("Patrick");
        second.setAge(SAMPLE_AGE);

        result = underTest.save(entity, SimpleEntity.class);
        secondResult = underTest.save(second, SimpleEntity.class);
        Thread.sleep(EVENTUAL_CONSISTENCY_PAUSE); //ensure datastore consistency.
    }

    @After
    public void tearDown() {

        underTest.delete(new SimpleEnitityKey(result.getEntityKey(), com.google.cloud.datastore.Key.class), SimpleEntity.class);
        underTest.delete(new SimpleEnitityKey(secondResult.getEntityKey(), com.google.cloud.datastore.Key.class), SimpleEntity.class);
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
    }

}
