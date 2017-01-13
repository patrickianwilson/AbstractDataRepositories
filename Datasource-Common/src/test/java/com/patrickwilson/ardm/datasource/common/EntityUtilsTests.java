package com.patrickwilson.ardm.datasource.common;
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
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEntityKey;

/**
 * Created by pwilson on 3/29/16.
 */
public class EntityUtilsTests {

    public static final ExplicitKeyEntity EXPLICIT_KEY_ENTITY = new ExplicitKeyEntity();
    public static final AnnotatedExplicitKeyEntity ANNOTATED_KEY_ENTITY = new AnnotatedExplicitKeyEntity();
    public static final AnnotatedImplicitKeyEntity ANNOTATED_IMPLICIT_KEY_ENTITY = new AnnotatedImplicitKeyEntity();

    static {
        EXPLICIT_KEY_ENTITY.setKey(new SimpleEntityKey<String>("test", String.class));
        ANNOTATED_IMPLICIT_KEY_ENTITY.setKey("test");
        ANNOTATED_KEY_ENTITY.setKey(new SimpleEntityKey("test", String.class, true));
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
                EntityKey key = EntityUtils.findEntityKeyType(entity);

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
            EntityUtils.findEntityKeyType(entity);
        }
    }

    @Test
    public void shouldFetchNullKeyValue() throws NoEntityKeyException {
        EntityKey key = EntityUtils.findEntityKeyType(annotatedButNullKeyEntity);

        Assert.assertNotNull(key);
        Assert.assertEquals(String.class, key.getKeyClass());
        Assert.assertNull(key.getKey());
    }

    @Test
    public void shouldUpdateTheKeyToValue() throws NoEntityKeyException {
        EntityUtils.updateEntityKey(annotatedButNullKeyEntity, new SimpleEntityKey("test", String.class));

        EntityKey key = EntityUtils.findEntityKeyType(annotatedButNullKeyEntity);

        Assert.assertNotNull(key);
        Assert.assertEquals(String.class, key.getKeyClass());
        Assert.assertEquals("test", key.getKey());
    }

    /**
     * For testing.
     */
    public static class ExplicitKeyEntity {

        private EntityKey<String> key;

        @Key(String.class)
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
        private EntityKey key;

        @Key(String.class)
        public EntityKey getKey() {
            return key;
        }

        public void setKey(EntityKey key) {
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

        @Key(String.class)
        public void setKey(String key) {
            this.key = key;
        }
    }



    /**
     * for testing.
     */
    public static class PrivateImplicitKey {
        @Key(String.class)
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
