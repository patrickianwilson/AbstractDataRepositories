package com.patrickwilson.ardm.datasource.common;

import com.google.common.base.Preconditions;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.annotation.Indexed;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

                    if (key != null) {
                         key.getKeyClass();
                    }

                    //explicit annotations will override a provided value (for consistency)
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
    public static Map<String, Object> fetchIndexableProperties(Object entity) {
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

    public static Map<String, Object> fetchAllProperties(Object entity) {
        Preconditions.checkNotNull(entity);

        HashMap<String, Object> result = new HashMap<>();

        PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(entity);
        for (PropertyDescriptor prop : props) {
            if ("class".equals(prop.getName())) {
                continue;
            }
            try {
                //extract the prop into the index map.
                Object val = prop.getReadMethod().invoke(entity);
                if (val != null) {
                    result.put(prop.getName(), val);
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new UnreadablePropertyException(String.format("Unable to read property '%s' from entity type %s", prop.getName(), entity.getClass().getName()));
            }
        }

        return result;
    }

    public static Map<String, Class> getPropertyTypeMap(Class entityClazz) {
        Preconditions.checkNotNull(entityClazz);

        HashMap<String, Class> result = new HashMap<>();

        PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(entityClazz);
        for (PropertyDescriptor prop : props) {
            if ("class".equals(prop.getName())) {
                //ignore getClass() method
                continue;
            }
            result.put(prop.getName(), prop.getPropertyType());
        }

        return result;
    }

    public static <ENTITY> Object rehydrateObject(Map<String, Object> props, Class<ENTITY> entityClazz) {
        Preconditions.checkNotNull(entityClazz);
        Preconditions.checkNotNull(props);


        PropertyDescriptor[] beanProps = PropertyUtils.getPropertyDescriptors(entityClazz);
        Object entity = null;
        try {
           entity = entityClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoDefaultConstructorException(entityClazz, e);
        }

        for (PropertyDescriptor prop: beanProps) {
            if (props.containsKey(prop.getName())) {
                //first make sure the incoming prop map actually contains this property.
                Object incomingVal = props.get(prop.getName());

                try {
                    if (prop.getWriteMethod() == null) {
                        throw new UnwritablePropertyException(String.format("The property %s does not have a Java Bean \"void set%s%s(%s)\" method", prop.getName(), prop.getName().substring(0, 1).toUpperCase(), prop.getName().substring(1), prop.getPropertyType().getName()));
                    }

                    if (Long.class.equals(incomingVal.getClass()) && (Short.class.equals(prop.getPropertyType()) || prop.getPropertyType().getName().equals("short"))) {
                        //sometimes databases only store Long values but entities could be narrower.
                        //handle Long -> short conversion.
                        prop.getWriteMethod().invoke(entity, ((Long) incomingVal).shortValue());
                    } else if (Long.class.equals(incomingVal.getClass()) && (Integer.class.equals(prop.getPropertyType()) || prop.getPropertyType().getName().equals("int"))) {
                        prop.getWriteMethod().invoke(entity, ((Long) incomingVal).intValue());
                    } else if (Calendar.class.equals(incomingVal.getClass()) && Date.class.equals(prop.getPropertyType())) {
                        prop.getWriteMethod().invoke(entity, new Date(((Calendar) incomingVal).getTimeInMillis()));
                    } else {
                        prop.getWriteMethod().invoke(entity, incomingVal);
                    }

                } catch (IllegalArgumentException e) {
                    LOG.debug("Encountered illegal arguement exception while setting ", e);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.info(String.format("unable to set property %s on entity type %s: %s", prop.getName(), entityClazz.getName(), e.getMessage()));
                }
            }
        }
        return entity;
    }

    public static Set<String> getchIndexablePropertyNames(Object entity) {
        Preconditions.checkNotNull(entity);

        HashSet<String> result = new HashSet<>();

        PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(entity);
        for (PropertyDescriptor prop : props) {
            if (
                    (prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Indexed.class))
                            || (prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Indexed.class))) {

                result.add(prop.getName());
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

    public static String getTableName(Class entityClazz) {
        Preconditions.checkNotNull(entityClazz);

        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new NotAnEntityException(entityClazz, "no @Entity Exception on the class");
        }

        Entity annotation = (Entity) entityClazz.getAnnotation(Entity.class);

        if (Entity.NO_DOMAIN_OR_TABLE.equals(annotation.domainOrTable())) {
            return entityClazz.getSimpleName();
        } else {
            return annotation.domainOrTable();
        }

    }
}
