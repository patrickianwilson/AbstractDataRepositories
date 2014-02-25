package com.patrickwilson.shared.util.test;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * A utily object to construct test data objects via a map of property names and object values.
 * User: pwilson
 * @param <T> a generic type.
 */
public final class BeanBuilder<T> {

    public static <T> BeanBuilder<T> newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {

        return new BeanBuilder(clazz);
    }


    private T inner;

    private BeanBuilder(Class<T> innerType) throws IllegalAccessException, InstantiationException {
        inner = innerType.newInstance();
    }

    public BeanBuilder<T> with(String key, Object value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyUtils.setProperty(inner, key, value);
        return this;
    }

    public BeanBuilder<T> with(String key, int value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyUtils.setProperty(inner, key, value);
        return this;
    }

    public BeanBuilder<T> with(String key, long value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyUtils.setProperty(inner, key, value);
        return this;
    }

    public BeanBuilder<T> with(String key, char value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyUtils.setProperty(inner, key, value);
        return this;
    }

    public BeanBuilder<T> with(String key, double value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyUtils.setProperty(inner, key, value);
        return this;
    }

    public BeanBuilder<T> fromObject(T object) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyUtils.copyProperties(inner, object);
        return this;
    }

    public <INNER> BeanBuilder<T> withSubObject(String key, Class<INNER> subObjectType, SubObjectTemplate<INNER> template) throws Exception {

        BeanBuilder<INNER> innerBeanBuilder = BeanBuilder.newInstance(subObjectType);
        template.defineObject(innerBeanBuilder);

        PropertyUtils.setProperty(inner, key, innerBeanBuilder.build());

        return this;
    }

    public <INNER_LIST extends Collection, INNER_LIST_ENTRY_TYPE> BeanBuilder<T> withAdditionalEntry(String key, Class<INNER_LIST> collectionType, Class<INNER_LIST_ENTRY_TYPE> entryType, SubObjectTemplate<INNER_LIST_ENTRY_TYPE> template) throws Exception {
        INNER_LIST collection = null;

        if (PropertyUtils.getProperty(inner, key) == null) {
            collection = collectionType.newInstance();
            PropertyUtils.setProperty(inner, key, collection);
        } else {
            collection = (INNER_LIST) PropertyUtils.getProperty(inner, key);
        }

        BeanBuilder<INNER_LIST_ENTRY_TYPE> innerBeanBuilder = BeanBuilder.newInstance(entryType);
        template.defineObject(innerBeanBuilder);

        collection.add(innerBeanBuilder.build());

        return this;
    }


    public T build() {
        return inner;
    }

    /**
     * a typesafe template to build an inner object with.
     * @param <INNER> the type of the inner object being constructed.
     */
    public interface SubObjectTemplate<INNER> {
        void defineObject(BeanBuilder<INNER> builder) throws Exception;
    }
}
