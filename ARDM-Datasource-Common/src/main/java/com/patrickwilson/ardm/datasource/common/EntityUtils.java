package com.patrickwilson.ardm.datasource.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import com.google.common.base.Preconditions;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.Key;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;

/**
 * This is a utility class for accessing common info from generic entities.
 */
public final class EntityUtils {

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
                    //this is the entity
                    PropertyUtils.getProperty(entity, prop.getName());
                    return (EntityKey) prop.getReadMethod().invoke(entity, new Object[]{});

                } else if ((prop.getReadMethod() != null && prop.getReadMethod().isAnnotationPresent(Key.class))
                        || prop.getWriteMethod() != null && prop.getWriteMethod().isAnnotationPresent(Key.class)) {
                    Object keyValue = prop.getReadMethod().invoke(entity, new Object[]{});
                    EntityKey key;
                    if (keyValue == null) {
                        key = new SimpleEnitityKey(null, Object.class);
                    } else {
                        key = new SimpleEnitityKey(keyValue, keyValue.getClass());
                    }
                    return key;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new NoEntityKeyException(entity, e);
        }

        throw new NoEntityKeyException(entity);
    }
}
