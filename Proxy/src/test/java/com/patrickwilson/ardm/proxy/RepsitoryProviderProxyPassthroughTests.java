package com.patrickwilson.ardm.proxy;
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
import com.patrickwilson.ardm.api.annotation.Attribute;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEntityKey;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests handling the situation where a repository was generated.
 * User: pwilson
 */
public class RepsitoryProviderProxyPassthroughTests extends BaseJMockTest {

     private final CRUDDatasourceAdaptor mockDataSource = createMock(CRUDDatasourceAdaptor.class);

    @Test
    public void testGenericsApi() {

        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository underTest = provider.bind(MyDataRepository.class).to(mockDataSource);

        final MyEntity entity = new MyEntity();

        entity.setAttrValue("A test value");
        entity.setId("row-id");


        addExpectations(new Expectations() { {
            oneOf(mockDataSource).save(entity, MyEntity.class);
                will(returnValue(entity));

            oneOf(mockDataSource).delete(with(EntityKeyMatcher.anEntityKey("row-id", String.class)), with(MyEntity.class));

            oneOf(mockDataSource).findOne(with(EntityKeyMatcher.anEntityKey("row-id", String.class)), with(MyEntity.class));
                will(returnValue(entity));

        } });


        final MyEntity saved = underTest.save(entity);
        underTest.delete(new SimpleEntityKey("row-id", String.class));
        
        Assert.assertTrue(entity.equals(saved));

        Assert.assertTrue(underTest.findOne(new SimpleEntityKey("row-id", String.class)).equals(saved));



    }

    @Test
    public void shouldGenerateKey() {
        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository underTest = provider.bind(MyDataRepository.class).to(mockDataSource);

        addExpectations(new Expectations() { {
            oneOf(mockDataSource).buildKey(with("abc123"), with(MyEntity.class));
                will(returnValue(new SimpleEntityKey("abc123", String.class, true)));
        } });

        EntityKey key = underTest.buildKey("abc123");
        Assert.assertEquals("abc123", key.getKey());
    }


    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity> {


    }

    /**
     * used for testing.
     */
    public static class MyEntity {

        @Attribute
        private String id;

        @Attribute
        private String attrValue;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAttrValue() {
            return attrValue;
        }

        public void setAttrValue(String attrValue) {
            this.attrValue = attrValue;
        }
    }

    /**
     * Utility class for quickly matching different subtypes of entity keys to make tests less brittle.
     */
    public static class EntityKeyMatcher extends BaseMatcher<EntityKey> {
        private EntityKey expected;

        public EntityKeyMatcher(EntityKey expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof EntityKey)) {
                return false;
            }
            EntityKey castedActual = (EntityKey) item;

            return castedActual.getKeyClass().equals(expected.getKeyClass())
                    && castedActual.getKey().equals(expected.getKey());

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("A Entity Key with value ")
                    .appendValue(expected.getKey())
                    .appendText(" and a key class of ")
                    .appendValue(expected.getKeyClass());
        }

        public static EntityKeyMatcher anEntityKey(EntityKey expected) {
            return new EntityKeyMatcher(expected);
        }

        public static EntityKeyMatcher anEntityKey(Object key, Class keyClass) {
            return new EntityKeyMatcher(new SimpleEntityKey(key, keyClass));
        }


    }


}
