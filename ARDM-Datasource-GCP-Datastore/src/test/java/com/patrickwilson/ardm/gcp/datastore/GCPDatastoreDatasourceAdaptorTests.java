package com.patrickwilson.ardm.gcp.datastore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;

/**
 * Created by pwilson on 12/22/16.
 */
public class GCPDatastoreDatasourceAdaptorTests {

    private static Datastore datastore;

    @BeforeClass
    public static void startupSuite() throws IOException {
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

    @Test
    public void doIt() {
        GCPDatastoreDatasourceAdaptor underTest = new GCPDatastoreDatasourceAdaptor(datastore);

        SimpleEntity entity = new SimpleEntity();
        entity.setEmail("patrick.ian.wilson@gmail.com");
        entity.setFirstName("Patrick");


        SimpleEntity result = underTest.save(entity, SimpleEntity.class);

        com.google.cloud.datastore.Key key = ((com.google.cloud.datastore.Key) result.getEntityKey());
        Assert.assertNotNull(key.getId());



    }



    @Entity(domainOrTable = "SimpleEntityForTesting")
    public static class SimpleEntity {

        private com.google.cloud.datastore.IncompleteKey entityKey;


        private String firstName;


        private String email;


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
    }

}
