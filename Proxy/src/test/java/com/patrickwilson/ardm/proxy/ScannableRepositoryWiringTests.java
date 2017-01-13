package com.patrickwilson.ardm.proxy;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;
import com.patrickwilson.ardm.api.annotation.Attribute;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.LinkedKey;
import com.patrickwilson.ardm.api.key.SimpleEntityKey;
import com.patrickwilson.ardm.api.repository.ScannableRepository;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.shared.util.test.BaseJMockTest;

/**
 * Created by pwilson on 1/5/17.
 */
public class ScannableRepositoryWiringTests extends BaseJMockTest {

    private final ScanableDatasourceAdaptor mockDataSource = createMock(ScanableDatasourceAdaptor.class);

    @Test
    public void shouldPassPrefixKeyBuildingDirectlyToAdaptor() {

        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository underTest = provider.bind(MyDataRepository.class).to(mockDataSource);

        addExpectations(new Expectations() { {
           oneOf(mockDataSource).buildPrefixKey(with((EntityKey) new SimpleEntityKey<String>("123", String.class, true)), with(MyEntity.class));
           SimpleEntityKey parent = new SimpleEntityKey<>("123", String.class, true);
           SimpleEntityKey id = new SimpleEntityKey(null, String.class);
           id.setLinkedKey(parent);
            will(returnValue(id));
        } });

        LinkedKey<String> key = underTest.buildPrefixKey(new SimpleEntityKey<>("123", String.class, true));

        Assert.assertNotNull(key.getLinkedKey());
        Assert.assertEquals("123", key.getLinkedKey().getKey());

    }


    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends ScannableRepository<RepositoryCorrectnessTests.MyEntity, String> {

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
