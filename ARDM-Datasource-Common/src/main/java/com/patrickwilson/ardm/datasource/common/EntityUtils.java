package com.patrickwilson.ardm.datasource.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;

/**
 * This is a utility class for accessing common info from generic entities.
 */
public final class EntityUtils {

    public static final Logger LOG = LoggerFactory.getLogger(EntityUtils.class);

    /**
     * reads the provided object entity and returns its key.
     * @return the com.patrickwilson.ardm.api.key.EntityKey of the entity
     */
    public static EntityKey findEntityKey(Object entity) throws NoEntityKeyException {
        Preconditions.checkNotNull(entity);

        try {


            PropertyDescriptor[] result  = PropertyUtils.getPropertyDescriptors(entity);
            for (PropertyDescriptor prop: result) {
                if (prop.getPropertyType().equals(EntityKey.class)) {
                    //this is the entity key.  might return null.

                    EntityKey key = (EntityKey) prop.getReadMethod().invoke(entity, new Object[]{});

                    Class keyValueClass = null;
                    if (prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class)) {
                        Key annotation = prop.getReadMethod().getAnnotation(Key.class);
                        if (annotation.keyClass() != null) {
                            keyValueClass = annotation.keyClass();
                        }
                    } else if (prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Key.class)) {
                        Key annotation = prop.getWriteMethod().getAnnotation(Key.class);
                        if (annotation.keyClass() != null) {
                            keyValueClass = annotation.keyClass();
                        }
                    }

                    if (keyValueClass == null) {
                        throw new NoEntityKeyException("Key Types of EntityKey must be annotated with @Key and the keyClass must be specified.  Ensure the @Key annotation is on either the getter or setter method (not the property)");
                    }

                    if (key == null) {
                        key = new SimpleEnitityKey(null, keyValueClass);
                    }

                    return key;

                } else if ((prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class))
                        || prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Key.class)) {
                    Object keyValue = prop.getReadMethod().invoke(entity, new Object[]{});
                    Class keyValueClass = null;
                    if (prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class)) {
                        Key annotation = prop.getReadMethod().getAnnotation(Key.class);
                        if (annotation.keyClass() != null) {
                            keyValueClass = annotation.keyClass();
                        }
                    } else {
                        Key annotation = prop.getWriteMethod().getAnnotation(Key.class);
                        if (annotation.keyClass() != null) {
                            keyValueClass = annotation.keyClass();
                        }
                    }

                    if (keyValueClass == null) {
                        if (keyValue != null) {
                            keyValueClass = keyValue.getClass();
                        } else {
                            keyValueClass = Object.class;
                        }
                    }

                    EntityKey key = new SimpleEnitityKey(keyValue, keyValueClass);

                    return key;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new NoEntityKeyException(entity, e);
        }

        throw new NoEntityKeyException(entity);
    }

    /**
     * reads the provided object entity and returns its key.
     * @return the com.patrickwilson.ardm.api.key.EntityKey of the entity
     */
    public static Class<?> findEntityKey(Class entityClazz) throws NoEntityKeyException {
        Preconditions.checkNotNull(entityClazz);

        PropertyDescriptor[] result  = PropertyUtils.getPropertyDescriptors(entityClazz);
        for (PropertyDescriptor prop: result) {
            if (prop.getPropertyType().equals(EntityKey.class)) {
                //this is the entity
                return EntityKey.class;

            } else if ((prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class))
                    || prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Key.class)) {

                Class keyValueClass = null;
                if (prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class)) {
                    Key annotation = prop.getReadMethod().getAnnotation(Key.class);
                    if (annotation.keyClass() != null) {
                        keyValueClass = annotation.keyClass();
                    }
                } else {
                    Key annotation = prop.getWriteMethod().getAnnotation(Key.class);
                    if (annotation.keyClass() != null) {
                        keyValueClass = annotation.keyClass();
                    }
                }

                if (keyValueClass == null) {
                    keyValueClass = prop.getPropertyType();
                }
                return keyValueClass;

            }
        }

        throw new NoEntityKeyException(entityClazz);
    }

    public static void updateEntityKey(Object entity, EntityKey key) throws NoEntityKeyException {
        try {
            PropertyDescriptor[] result  = PropertyUtils.getPropertyDescriptors(entity);
            for (PropertyDescriptor prop: result) {
                if (prop.getPropertyType().equals(EntityKey.class)) {
                    //this is the entity
                    PropertyUtils.setProperty(entity, prop.getName(), key);
                    return;
                } else if ((prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class))
                        || prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Key.class)) {
                    PropertyUtils.setProperty(entity, prop.getName(), key.getKey());
                    return;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new NoEntityKeyException(entity, e);
        }

        throw new NoEntityKeyException(entity);
    }

    /**
     * These are non composite values.  Cant index and "object" but the object could be broken down instead.
     * returns a map indexed by a field descriptor.  sub objects are indexed by <parent>.<child>.
     *
     * Can't index multiple children of the same type.
     */
    public static Map<String, Object> FetchIndexableProperties(Object entity) {
        Preconditions.checkNotNull(entity);

        HashMap<String, Object> result = new HashMap<>();

        PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(entity);
        for (PropertyDescriptor prop : props) {
            if (
                    (prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Indexed.class))
                            || (prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Indexed.class))) {

                indexValue(prop, entity, result);
            }
        }

        return result;
    }

    private static void indexValue(PropertyDescriptor prop, Object entity, HashMap<String, Object> result) {
        Class<?> propType = prop.getPropertyType();
        if (!propType.isPrimitive() && propType != String.class) {
            LOG.info("Indexing on sub objects not currently supported.  Dropping {} from the index.", prop.getName());
        }
        try {
            //extract the prop into the index map.
            result.put(prop.getName(), prop.getReadMethod().invoke(entity));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnreadablePropertyException(String.format("Unable to read indexed property '%s' from entity type %s", prop.getName(), entity.getClass().getName()));
        }
    }
}
