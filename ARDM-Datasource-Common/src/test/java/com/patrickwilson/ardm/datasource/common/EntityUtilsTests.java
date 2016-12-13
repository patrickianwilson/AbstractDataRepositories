package com.patrickwilson.ardm.datasource.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;

/**
 * Created by pwilson on 3/29/16.
 */
public class EntityUtilsTests {

    public static final ExplicitKeyEntity EXPLICIT_KEY_ENTITY = new ExplicitKeyEntity();
    public static final AnnotatedExplicitKeyEntity ANNOTATED_KEY_ENTITY = new AnnotatedExplicitKeyEntity();
    public static final AnnotatedImplicitKeyEntity ANNOTATED_IMPLICIT_KEY_ENTITY = new AnnotatedImplicitKeyEntity();

    static {
        EXPLICIT_KEY_ENTITY.setKey(new SimpleEnitityKey<String>("test", String.class));
        ANNOTATED_IMPLICIT_KEY_ENTITY.setKey("test");
        ANNOTATED_KEY_ENTITY.setKey("test");
    }

    public static final Object[] entities = new Object[] {
            EXPLICIT_KEY_ENTITY,
            ANNOTATED_KEY_ENTITY,

    };


    public static PrivateImplicitKey PRIVATE_KEY_ENTITY = new PrivateImplicitKey();
    public static NoKeyAnnotatedOrDefined NO_KEY_ENTITY = new NoKeyAnnotatedOrDefined();

    static {
        NO_KEY_ENTITY.setKey("test");
    }

    public static Object[] invalidEntities = new Object[] {
            PRIVATE_KEY_ENTITY,
            NO_KEY_ENTITY,
            ANNOTATED_IMPLICIT_KEY_ENTITY
    };

    public static AnnotatedExplicitKeyEntity ANNOTATED_BUT_NULL_KEY_ENTITY = new AnnotatedExplicitKeyEntity();

    @Before
    public void setup() {
        ANNOTATED_BUT_NULL_KEY_ENTITY = new AnnotatedExplicitKeyEntity();
    }

    @Test
    public void fetchKeyInAllDecalarationMethods() {
        for (Object entity: entities) {
            try {
                EntityKey key = EntityUtils.findEntityKey(entity);

                Assert.assertNotNull(key);
                Assert.assertNotNull(key.getKey());
                Assert.assertEquals(String.class, key.getKeyClass());

            } catch (NoEntityKeyException e) {
                e.printStackTrace();
                Assert.fail(String.format("Key was not found or accessible: %s, for entity type %s", e.getMessage(), entity.getClass().getName()));
            }

        }
    }

    @Test(expected = NoEntityKeyException.class)
    public void shouldThrowNoKeyException() throws NoEntityKeyException {
        for (Object entity: invalidEntities) {
            EntityUtils.findEntityKey(entity);
        }
    }

    @Test
    public void shouldFetchNullKeyValue() throws NoEntityKeyException {
        EntityKey key = EntityUtils.findEntityKey(ANNOTATED_BUT_NULL_KEY_ENTITY);

        Assert.assertNotNull(key);
        Assert.assertEquals(String.class, key.getKeyClass());
        Assert.assertNull(key.getKey());
    }

    @Test
    public void shouldUpdateTheKeyToValue() throws NoEntityKeyException {
        EntityUtils.updateEntityKey(ANNOTATED_BUT_NULL_KEY_ENTITY, new SimpleEnitityKey("test", String.class));

        EntityKey key = EntityUtils.findEntityKey(ANNOTATED_BUT_NULL_KEY_ENTITY);

        Assert.assertNotNull(key);
        Assert.assertEquals(String.class, key.getKeyClass());
        Assert.assertEquals("test", key.getKey());
    }

    public static class ExplicitKeyEntity {

        private EntityKey<String> key;

        @Key(keyClass = String.class)
        public EntityKey<String> getKey() {
            return key;
        }

        public void setKey(EntityKey<String> key) {
            this.key = key;
        }
    }

    public static class AnnotatedExplicitKeyEntity {
        private String key;

        @Key(keyClass = String.class)
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class AnnotatedImplicitKeyEntity {
        private String key;

        public String getKey() {
            return key;
        }

        @Key
        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class PrivateImplicitKey {
        @Key
        private String key;

        private String getKey() {
            return null;
        }
    }

    public static class NoKeyAnnotatedOrDefined {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
