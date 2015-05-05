package com.patrickwilson.ardm.samples.simpledb;

import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.key.EntityKey;

/**
 * Created by pwilson on 5/5/15.
 */
@Entity(domainOrTable = SimpleDBExample.DOMAIN_NAME)
//@Expire()  TODO
public class User {

    private String firstName;
    private int age;
    private EntityKey<String> primaryKey;


    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public EntityKey<String> getPrimaryKey() {
        return primaryKey;
    }

    public User setPrimaryKey(EntityKey<String> primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }
}
