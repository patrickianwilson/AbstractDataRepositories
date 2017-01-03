package com.patrickwilson.ardm.datasource.memory;

import java.util.Comparator;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.common.EntityUtils;
import com.patrickwilson.ardm.datasource.common.NoEntityKeyException;

/**
 * Created by pwilson on 12/10/16.
 */
public class GenericKeyComparator implements Comparator<EntityKey<Comparable>> {


    public GenericKeyComparator(Class<?> clazz) throws NoEntityKeyException {

        Class keyClazz = EntityUtils.findEntityKey(clazz);



        if (!EntityKey.class.equals(keyClazz) && !Comparable.class.isAssignableFrom(keyClazz)) {
            throw new RepositoryEntityException("Entity key type must implement comparable to be used in the InMemory database.");
        }

    }

    @Override
    public int compare(EntityKey<Comparable> key1, EntityKey<Comparable> key2) {
        return key1.getKey().compareTo(key2.getKey());
    }

}
