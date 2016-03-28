package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.annotation.Attribute;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
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
        underTest.delete(new SimpleEnitityKey<String>("row-id", String.class));
        
        Assert.assertTrue(entity.equals(saved));

        Assert.assertTrue(underTest.findOne(new SimpleEnitityKey<String>("row-id", String.class)).equals(saved));



    }


    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity, String> {


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
            return new EntityKeyMatcher(new SimpleEnitityKey(key, keyClass));
        }


    }


}
