package com.patrickwilson.ardm.samples.simpledb;

import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;
import org.junit.Assert;
import org.junit.Test;
import com.patrickwilson.ardm.datasource.memory.InMemoryDatsourceAdaptor;
import com.patrickwilson.ardm.proxy.RepositoryProvider;

/**
 * Created by pwilson on 12/10/16.
 */
public class UserSampleTests {

    public static final int SAMPLE_AGE = 32;

    @Test
    public void doWithInMemoryAdaptor() {

        RepositoryProvider provider = new RepositoryProvider();
        UserRepository repo = provider.bind(UserRepository.class).to(new InMemoryDatsourceAdaptor());

        User me = repo.save(new User().setAge(SAMPLE_AGE).setFirstName("Patrick"));


        Assert.assertNotNull(me);

        List<User> results = repo.findByFirstName("Patrick");

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
    }


}
