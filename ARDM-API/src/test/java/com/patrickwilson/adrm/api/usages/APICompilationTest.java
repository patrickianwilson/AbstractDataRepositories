package com.patrickwilson.adrm.api.usages;

import org.junit.Test;
import com.patrickwilson.ardm.api.annotation.Attribute;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.repository.CRUDRepository;

/**
 * This simply tests that the API still compiles in its intended usage.
 */
public class APICompilationTest {

    @Test
    public void doesThisCompile() {

    }

    /**
     * for testing.
     */
    @Entity
    public static class UserEntity {

        @Attribute
        private EntityKey<String> key;  //we know this is the key for the entity based on the type.

        @Attribute
        private String email;

        @Attribute(dbColumn = "firstName")
        private String fname;

        @Attribute(dbColumn = "lastName")
        private String lname;

        public EntityKey<String> getKey() {
            return key;
        }

        public void setKey(EntityKey<String> key) {
            this.key = key;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }
    }

    private interface UserRepository extends CRUDRepository {

    }

}
