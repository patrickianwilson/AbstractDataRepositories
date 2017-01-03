package com.patrickwilson.ardm.samples.inmemory;

import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.Key;

/**
 * Created by pwilson on 5/5/15.
 */
@Entity
//@Expire()  TODO
public class User {

    private String firstName;
    private int age;

    private String primaryKey;

    @Indexed
    public String getFirstName() {
        return firstName;
    }

    @Indexed
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

    @Key(keyClass = String.class)
    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
