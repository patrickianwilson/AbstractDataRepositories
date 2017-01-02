package com.patrickwilson.ardm.gcp.datastore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.patrickwilson.ardm.datasource.api.query.*;
import org.junit.*;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.KeyFactory;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptorTests {

    public static final short SAMPLE_AGE = 35;
    private static Datastore datastore;

    @BeforeClass
    public static void startupSuite() throws IOException {

        datastore = setupFromEnvironment();

        if (datastore != null) {
            //success - likely running emulator or GCP.
            return;
        }
        InputStream credStream = ClassLoader.getSystemResourceAsStream("datastore-service-account.json.creds");
        String projectId = null;
        if (credStream == null) {
            //try to locate a file based on a env variable
            String credFile = System.getProperty("GoogleServiceAccountCredentialFile");
            if (credFile != null) {
                credStream = new FileInputStream(credFile);

            }

            projectId = System.getProperty("GoogleProjectId");
        }

        Assume.assumeNotNull(credStream); //will skip this test suite if there are no credentials.
        Assume.assumeNotNull(projectId);

        datastore = DatastoreOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(credStream))
                .setProjectId(projectId)
                .build()
                .getService();

    }

    private static Datastore setupFromEnvironment() {

        return DatastoreOptions.newBuilder().build().getService();

    }

    private GCPDatastoreDatasourceAdaptor underTest = null;
    private SimpleEntity result = null;
    private SimpleEntity secondResult = null;

    @Before
    public void setup () throws InterruptedException {

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
        Thread.sleep(500); //ensure datastore consistency.
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

        QueryData query = new QueryData(new QueryPage(0, 10), firstNameCriteria);
        query.setParameters(new Object[] { "Patrick" });

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

        QueryData query = new QueryData(new QueryPage(0, 10), firstNameCriteria);
        query.setParameters(new Object[] {"Patrick", "patrick.ian.wilson@gmail.com"});

        QueryResult<SimpleEntity> qResult = underTest.findByCriteria(query, SimpleEntity.class);

        Assert.assertNotNull(qResult);
        Assert.assertEquals(1, qResult.getNumResults());
        Assert.assertNotNull(qResult.getResults());
        Assert.assertEquals(1, qResult.getResults().size());
    }



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
