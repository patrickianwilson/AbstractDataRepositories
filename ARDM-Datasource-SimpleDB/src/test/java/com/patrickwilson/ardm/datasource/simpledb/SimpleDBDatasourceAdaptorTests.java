package com.patrickwilson.ardm.datasource.simpledb;

import java.io.InputStream;
import java.util.Scanner;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsyncClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.key.EntityKey;

/**
 * Functional tests that excersize the simple db APIs.
 */
public class SimpleDBDatasourceAdaptorTests {

    public static final String DOMAIN_NAME = "Patrick_Test_Domain";
    private static AmazonSimpleDBAsync client = null;

    private static SimpleDBDatasourceAdaptor underTest;

    @BeforeClass
    public static void setupClass() {
        InputStream credentials = SimpleDBDatasourceAdaptorTests.class.getResourceAsStream("/creds/aws.credentials");

        if (credentials == null) {
             throw new RuntimeException("Please put a aws credentials file in [ARDM-Datasource-SimpleDB/src/test/resources/creds] to run the ARDM datasource test for Simple DB.");
        }

        Scanner credentialScanner = new Scanner(credentials);

        //first line is the keyId, second line is the secret.
        String keyId = credentialScanner.nextLine();
        String secret = credentialScanner.nextLine();
        AWSCredentials awsCredentials = new BasicAWSCredentials(keyId, secret);

        client = new AmazonSimpleDBAsyncClient(awsCredentials);

        //create a domain for testing...
        client.createDomain(new CreateDomainRequest().withDomainName(DOMAIN_NAME));

    }

    @AfterClass
    public static void tearDownClass() {
        client.deleteDomain(new DeleteDomainRequest().withDomainName(DOMAIN_NAME));
    }

    @Before
    public void setup() {
        underTest = new SimpleDBDatasourceAdaptor(client);
    }

    @Test
    public void doesSaSampleEntityveWork() {

        SampleEntity result = underTest.save(new SampleEntity().setIntegerField(12).setStringField("my string"), SampleEntity.class);

        Assert.assertNotNull("primary key was null", result.getPrimaryKey());
        Assert.assertNotNull("primary key value was null", result.getPrimaryKey().getKey());

        SampleEntity fetchedResult = underTest.findOne(result.getPrimaryKey(), SampleEntity.class);

        Assert.assertNotNull(fetchedResult);
        Assert.assertEquals("The Entity that was saved does not match the entity that was fetched", result, fetchedResult);

    }

    @Entity(domainOrTable = DOMAIN_NAME)
    public static class SampleEntity {
        private String stringField;
        private int integerField;
        private EntityKey<String> primaryKey;

        private String id;

        public String getStringField() {
            return stringField;
        }

        public SampleEntity setStringField(String stringField) {
            this.stringField = stringField;
            return this;
        }

        public int getIntegerField() {
            return integerField;
        }

        public SampleEntity setIntegerField(int integerField) {
            this.integerField = integerField;
            return this;
        }

        public EntityKey<String> getPrimaryKey() {
            return primaryKey;
        }

        public SampleEntity setPrimaryKey(EntityKey<String> primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SampleEntity that = (SampleEntity) o;

            if (integerField != that.integerField) return false;
            if (stringField != null ? !stringField.equals(that.stringField) : that.stringField != null) return false;
            if (primaryKey != null ? !primaryKey.equals(that.primaryKey) : that.primaryKey != null) return false;
            return !(id != null ? !id.equals(that.id) : that.id != null);

        }

        @Override
        public int hashCode() {
            int result = stringField != null ? stringField.hashCode() : 0;
            result = 31 * result + integerField;
            result = 31 * result + (primaryKey != null ? primaryKey.hashCode() : 0);
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SampleEntity{" +
                    "stringField='" + stringField + '\'' +
                    ", integerField=" + integerField +
                    ", primaryKey=" + primaryKey +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
}
