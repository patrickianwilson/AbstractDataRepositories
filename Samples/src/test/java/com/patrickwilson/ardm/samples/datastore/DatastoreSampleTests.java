package com.patrickwilson.ardm.samples.datastore;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.annotation.Query;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.datasource.gcp.datastore.GCPDatastoreDatasourceAdaptor;
import com.patrickwilson.ardm.proxy.RepositoryProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by pwilson on 1/2/17.
 */
public class DatastoreSampleTests {

    public static final int SAMPLE_AGE = 32;

    @Test
    public void doWithDatastoreEmulatorAdaptor() {

        RepositoryProvider provider = new RepositoryProvider();

        Datastore client = DatastoreOptions.newBuilder().build().getService();


        UserRepository repo = provider.bind(UserRepository.class).to(new GCPDatastoreDatasourceAdaptor(client));

        User me = repo.save(new User.Builder().setAge(SAMPLE_AGE).setFirstName("Patrick").build());

        User other = repo.save(new User.Builder().setAge(SAMPLE_AGE).setFirstName("Matthew").build());

        Assert.assertNotNull(me);
        Assert.assertNotNull(other);

        List<User> results = repo.findByFirstName("Patrick");

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

        results = repo.findByFirstNameAndAge("Matthew", SAMPLE_AGE);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());


        repo.delete(me);
        repo.delete(other);
    }


    /**
     * for testing.
     */
    @Entity
    public static class User {

        private String firstName;
        private int age;

        private com.google.cloud.datastore.Key primaryKey;

        @Indexed
        public String getFirstName() {
            return firstName;
        }

        @Indexed
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @Indexed
        public int getAge() {
            return age;
        }

        @Indexed
        public void setAge(int age) {
            this.age = age;
        }

        @Key(keyClass = com.google.cloud.datastore.Key.class)
        public com.google.cloud.datastore.Key getPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(com.google.cloud.datastore.Key primaryKey) {
            this.primaryKey = primaryKey;
        }

        /**
         * A Builder.
         */
        public static class Builder {
            private User instance = new User();

            public Builder setFirstName(String firstName) {
                instance.setFirstName(firstName);
                return this;
            }

            public Builder setAge(int age) {
                instance.setAge(age);
                return this;
            }

            public Builder setPrimaryKey(com.google.cloud.datastore.Key primaryKey) {
                instance.setPrimaryKey(primaryKey);
                return this;
            }

            public User build() {
                return instance;
            }
        }
    }


    /**
     * For testing.
     */
    @Repository(User.class)
    public interface UserRepository extends CRUDRepository<User, Key> {

        @Query
        List<User> findByFirstName(String fname);

        @Query
        List<User> findByFirstNameAndAge(String fname, int age);


    }
}
