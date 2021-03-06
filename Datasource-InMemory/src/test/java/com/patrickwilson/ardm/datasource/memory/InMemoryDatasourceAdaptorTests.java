package com.patrickwilson.ardm.datasource.memory;
/*
 The MIT License (MIT)

 Copyright (c) 2014 Patrick Wilson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryLogicTree;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.ardm.datasource.api.query.ValueEqualsLogicTreeNode;


/**
 * Tests for the InMemory Datasource reference implementation.
 */
public class InMemoryDatasourceAdaptorTests {

    private InMemoryDatsourceAdaptor underTest = new InMemoryDatsourceAdaptor();
    private static final int SAMPLE_AGE = 35;

    @Before
    public void setup() {
        underTest.clearDatabase();

        SampleEntity entity1 = new SampleEntity();
        entity1.setValue("My Value");
        underTest.save(entity1, SampleEntity.class);

        SampleEntity entity2 = new SampleEntity();
        entity2.setValue("My Second Value");
        underTest.save(entity2, SampleEntity.class);

        UserEntity patrick = new UserEntity();

        patrick.age = SAMPLE_AGE;
        patrick.fname = "Patrick";
        patrick.lname = "Wilson";

        underTest.save(patrick, UserEntity.class);
    }



    @Test
    public void shouldSaveAndFetchAndDelete() {

        SampleEntity entity1 = new SampleEntity();
        entity1.setValue("My Different Value");

        SampleEntity updatedEntity = underTest.save(entity1, SampleEntity.class);
        Assert.assertNotNull(updatedEntity);
        Assert.assertEquals("My Different Value", updatedEntity.getValue());
        Assert.assertNotNull(updatedEntity.getKey());

        EntityKey providedKey = updatedEntity.getKey();
        updatedEntity.setValue("Updated Value");
        underTest.save(updatedEntity, SampleEntity.class);

        Assert.assertNotNull(updatedEntity);
        Assert.assertEquals("Updated Value", updatedEntity.getValue());
        Assert.assertEquals(providedKey, updatedEntity.getKey());

        Assert.assertNotNull(underTest.findOne(providedKey, SampleEntity.class));

        underTest.delete(providedKey, SampleEntity.class);
        Assert.assertNull(underTest.findOne(providedKey, SampleEntity.class));

    }

    @Test
    public void shouldRetriveAllEntities() {

        QueryResult<SampleEntity> allEntities = underTest.findAll(SampleEntity.class);
        Assert.assertNotNull(allEntities);
        Assert.assertEquals(2, allEntities.getNumResults());
        Assert.assertEquals(0, allEntities.getStartIndex());
        Assert.assertNotNull(allEntities.getResults());
        Assert.assertEquals(2, allEntities.getResults().size());

        for (SampleEntity ent: allEntities.getResults()) {
            Assert.assertNotNull(ent.getKey());
            Assert.assertNotNull(ent.getValue());
        }

        //currently no explicit guarentee of order...

    }

    @Test
    public void shouldFetchEntitiesByQuery() {
        QueryData queryData = new QueryData();
        queryData.setParameters(new Object[]{"My Value"});

        QueryLogicTree query = new QueryLogicTree();

        ValueEqualsLogicTreeNode valueEqClause = new ValueEqualsLogicTreeNode();
        valueEqClause.setColumnName("value");
        valueEqClause.setValueArgIndex(0);

        query.setRootCriteria(valueEqClause);
        queryData.setCriteria(query);
        // deliberately not set a page object - should handle gracefully.
        //queryData.setPage(new QueryPage());
        QueryResult<SampleEntity> result = underTest.findByCriteria(queryData, SampleEntity.class);

        Assert.assertNotNull("Find By Criteria Returned a Null Result.", result);
        Assert.assertEquals("Wrong Number of Results", 1, result.getResults().size());
        Assert.assertEquals("Mismatch between number of actual results and number reported on QueryResult object.", 1, result.getNumResults());


    }

    @Test
    public void shouldHaveSeperateTables() {
        QueryResult<SampleEntity> allEntities = underTest.findAll(SampleEntity.class);
        Assert.assertEquals(2, allEntities.getNumResults());

        QueryResult<UserEntity> allUsers = underTest.findAll(UserEntity.class);
        Assert.assertEquals(1, allUsers.getNumResults());
    }


    /**
     * For testing.
     */
    @Entity
    public static class SampleEntity {
        private EntityKey key;

        private String value;

        @Key(String.class)
        public EntityKey getKey() {
            return key;
        }

        public void setKey(EntityKey key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        @Indexed
        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * for testing.
     */
    @Entity
    public static class UserEntity {
        private String fname;
        private String lname;
        private long age;
        private EntityKey key;

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

        public long getAge() {
            return age;
        }

        public void setAge(long age) {
            this.age = age;
        }

        public EntityKey getKey() {
            return key;
        }

        @Key(String.class)
        public void setKey(EntityKey key) {
            this.key = key;
        }
    }

}
