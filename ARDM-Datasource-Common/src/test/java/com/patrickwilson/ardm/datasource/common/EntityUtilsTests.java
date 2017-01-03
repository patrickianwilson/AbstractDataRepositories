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

    public static final Object[] ENTITIES = new Object[] {
            EXPLICIT_KEY_ENTITY,
            ANNOTATED_KEY_ENTITY,

    };


    public static final PrivateImplicitKey PRIVATE_KEY_ENTITY = new PrivateImplicitKey();
    public static final NoKeyAnnotatedOrDefined NO_KEY_ENTITY = new NoKeyAnnotatedOrDefined();

    static {
        NO_KEY_ENTITY.setKey("test");
    }

    public static final Object[] INVALID_ENTITIES = new Object[] {
            PRIVATE_KEY_ENTITY,
            NO_KEY_ENTITY,
            ANNOTATED_IMPLICIT_KEY_ENTITY
    };

    private static AnnotatedExplicitKeyEntity annotatedButNullKeyEntity = new AnnotatedExplicitKeyEntity();

    @Before
    public void setup() {
        annotatedButNullKeyEntity = new AnnotatedExplicitKeyEntity();
    }

    @Test
    public void fetchKeyInAllDecalarationMethods() {
        for (Object entity: ENTITIES) {
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
        for (Object entity: INVALID_ENTITIES) {
            EntityUtils.findEntityKey(entity);
        }
    }

    @Test
    public void shouldFetchNullKeyValue() throws NoEntityKeyException {
        EntityKey key = EntityUtils.findEntityKey(annotatedButNullKeyEntity);

        Assert.assertNotNull(key);
        Assert.assertEquals(String.class, key.getKeyClass());
        Assert.assertNull(key.getKey());
    }

    @Test
    public void shouldUpdateTheKeyToValue() throws NoEntityKeyException {
        EntityUtils.updateEntityKey(annotatedButNullKeyEntity, new SimpleEnitityKey("test", String.class));

        EntityKey key = EntityUtils.findEntityKey(annotatedButNullKeyEntity);

        Assert.assertNotNull(key);
        Assert.assertEquals(String.class, key.getKeyClass());
        Assert.assertEquals("test", key.getKey());
    }

    /**
     * For testing.
     */
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

    /**
     * for testing.
     */
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


    /**
     * for testing.
     */
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

    /**
     * for testing.
     */
    public static class PrivateImplicitKey {
        @Key
        private String key;

        private String getKey() {
            return null;
        }
    }

    /**
     * for testing.
     */
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
