package com.patrickwilson.ardm.samples.simpledb;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.patrickwilson.ardm.datasource.memory.InMemoryDatsourceAdaptor;
import com.patrickwilson.ardm.proxy.RepositoryProvider;

/**
 * Created by pwilson on 12/10/16.
 */
public class UserSampleTests {

    @Test
    public void setup() {

        RepositoryProvider provider = new RepositoryProvider();
        UserRepository repo = provider.bind(UserRepository.class).to(new InMemoryDatsourceAdaptor());

        User me = repo.save(new User().setAge(32).setFirstName("Patrick"));


        Assert.assertNotNull(me);

        List<User> results = repo.findByFirstName("Patrick");

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
    }

}
