package com.patrickwilson.ardm.gcp.datastore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.patrickwilson.ardm.datasource.api.query.QueryResult;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
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
        String projectId = "inlaid-citron-94802";
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
        Map<String, String> envmap = System.getenv();
        if (envmap.containsKey("DATASTORE_PROJECT_ID") && envmap.containsKey("DATASTORE_HOST")) {
            return DatastoreOptions.newBuilder().build().getService();
        }
        return null;
    }

    @Test
    public void doIt() {
        GCPDatastoreDatasourceAdaptor underTest = new GCPDatastoreDatasourceAdaptor(datastore);

        SimpleEntity entity = new SimpleEntity();
        entity.setEmail("patrick.ian.wilson@gmail.com");
        entity.setFirstName("Patrick");
        entity.setAge(SAMPLE_AGE);


        SimpleEntity result = underTest.save(entity, SimpleEntity.class);

        com.google.cloud.datastore.Key key = ((com.google.cloud.datastore.Key) result.getEntityKey());
        Assert.assertNotNull(key.getId());

        SimpleEntity fromDB = underTest.findOne(new SimpleEnitityKey(key, com.google.cloud.datastore.Key.class), SimpleEntity.class);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(result.email, fromDB.email);
        Assert.assertEquals(result.firstName, fromDB.firstName);
        Assert.assertEquals(result.age, fromDB.age);


        QueryResult<SimpleEntity> allEntities = underTest.findAll(SimpleEntity.class);

        Assert.assertNotNull(allEntities);
        Assert.assertEquals(1, allEntities.getNumResults());
        Assert.assertNotNull(allEntities.getResults());
        Assert.assertEquals(1, allEntities.getResults().size());

        underTest.delete(new SimpleEnitityKey(key, com.google.cloud.datastore.Key.class), SimpleEntity.class);


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
