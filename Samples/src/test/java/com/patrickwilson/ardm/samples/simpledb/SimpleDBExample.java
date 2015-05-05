package com.patrickwilson.ardm.samples.simpledb;

import java.io.InputStream;
import java.util.Scanner;
import org.junit.AfterClass;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsyncClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.simpledb.SimpleDBDatasourceAdaptor;
import com.patrickwilson.ardm.proxy.RepositoryProvider;

/**
 * Created by pwilson on 5/5/15.
 */
public class SimpleDBExample {

    public static final String DOMAIN_NAME = "Patrick_Test_Domain";
    private AmazonSimpleDBAsync client = null;

    UserRepository repository;

    public static void main(String ... args) {
        SimpleDBExample main = new SimpleDBExample();
        try {
            main.setupAWS();

            main.startListening();



        } finally {
            main.tearDownAWS();
        }



    }


    public void startListening() {
        Scanner stdIn = new Scanner(System.in);
        System.out.println("Options:");
        System.out.println("1. List user with ID.[<id>]");
        System.out.println("2. Create User. [<firstname> <age>]");

        while(stdIn.hasNextLine()) {
            String line = stdIn.nextLine();
            if ("quit".equals(line)) {
                return;
            }

            String[] lineParts = line.split(" ");
            if ("1".equals(lineParts[0])) {
                User user = repository.findOne(new SimpleEnitityKey(lineParts[1], String.class));
                System.out.println("Found User: FirstName= " + user.getFirstName() + ", age=" + user.getAge());
                continue;

            } else if ("2".equals(lineParts[0])) {
                User user = new User().setAge(Integer.parseInt(lineParts[2])).setFirstName(lineParts[1]);
                User persisted = repository.save(user);
                System.out.println("Successfully Created User with Id: " + persisted.getPrimaryKey().getKey());
                continue;
            }
        }

    }


    public void setupAWS() {
        InputStream credentials = SimpleDBExample.class.getResourceAsStream("/creds/aws.credentials");

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

        RepositoryProvider provider = new RepositoryProvider();
        repository = provider.bind(UserRepository.class).to(new SimpleDBDatasourceAdaptor(client));

        //.cacheWith(CacheProvider).expireWith(ExpiryPolicy.{RESET_ON_GET|AT_TIME})

    }



    @AfterClass
    public void tearDownAWS() {

        client.deleteDomain(new DeleteDomainRequest().withDomainName(DOMAIN_NAME));
    }


}
