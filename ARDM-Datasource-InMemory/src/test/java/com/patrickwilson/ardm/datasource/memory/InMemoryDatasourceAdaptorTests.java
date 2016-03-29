package com.patrickwilson.ardm.datasource.memory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;


/**
 * Tests for the InMemory Datasource reference implementation.
 */
public class InMemoryDatasourceAdaptorTests {

    private InMemoryDatsourceAdaptor underTest = new InMemoryDatsourceAdaptor();

    @Before
    public void setup() {
        underTest.clearDatabase();
    }

    @Test
    public void shouldSaveAndFetchAndDelete() {
        SampleEntity entity1 = new SampleEntity();
        entity1.setValue("My Value");

        SampleEntity updatedEntity = underTest.save(entity1, SampleEntity.class);
        Assert.assertNotNull(updatedEntity);
        Assert.assertEquals("My Value", updatedEntity.getValue());
        Assert.assertNotNull(updatedEntity.getKey());

        String providedKey = updatedEntity.getKey();
        updatedEntity.setValue("Updated Value");
        underTest.save(updatedEntity, SampleEntity.class);

        Assert.assertNotNull(updatedEntity);
        Assert.assertEquals("Updated Value", updatedEntity.getValue());
        Assert.assertEquals(providedKey, updatedEntity.getKey());

        Assert.assertNotNull(underTest.findOne(new SimpleEnitityKey(providedKey, String.class), SampleEntity.class));

        underTest.delete(new SimpleEnitityKey(providedKey, String.class), SampleEntity.class);
        Assert.assertNull(underTest.findOne(new SimpleEnitityKey(providedKey, String.class), SampleEntity.class));

    }

    @Entity
    public static class SampleEntity {
        private String key;
        private String value;

        @Key(keyClass = String.class)
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
