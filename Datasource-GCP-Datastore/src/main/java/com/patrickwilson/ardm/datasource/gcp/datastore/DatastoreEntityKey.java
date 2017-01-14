package com.patrickwilson.ardm.datasource.gcp.datastore;

import java.util.Objects;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.LinkedKey;
import com.patrickwilson.ardm.api.key.SimpleEntityKey;

/**
 * Created by pwilson on 1/13/17.
 */
public class DatastoreEntityKey implements LinkedKey {

    private IncompleteKey key;

    public DatastoreEntityKey(IncompleteKey key) {
        this.key = key;
    }

    @Override
    public EntityKey getLinkedKey() {
        if (key.getParent() != null) {
            return new DatastoreEntityKey(key.getParent());
        }
        return null;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Class<?> getKeyClass() {
        return Key.class;
    }

    @Override
    public boolean isPopulated() {
        return (key instanceof Key);
    }

    @Override
    public int compareTo(SimpleEntityKey o) {
        return 0; //not comparable.
    }

    //CheckStyle:OFF

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatastoreEntityKey that = (DatastoreEntityKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "DatastoreEntityKey{" +
                "key=" + key +
                '}';
    }

    //CheckStyle:ON
}


