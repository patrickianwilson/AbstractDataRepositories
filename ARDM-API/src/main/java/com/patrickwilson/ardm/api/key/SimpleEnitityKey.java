package com.patrickwilson.ardm.api.key;

/**
 * An simple version of an entity key that attempts to save return a generic key in the requested format.
 * User: pwilson
 * @param <KEY_TYPE> a generic type for the actual key.
 */
public class SimpleEnitityKey<KEY_TYPE> implements EntityKey<KEY_TYPE> {

    private Class<KEY_TYPE> keyTypeClass;
    private KEY_TYPE key;

    public SimpleEnitityKey(KEY_TYPE key, Class<KEY_TYPE> keyTypeClass) {
        this.keyTypeClass = keyTypeClass;
        this.key = key;
    }

    @Override
    public Class<KEY_TYPE> getKeyClass() {
        return keyTypeClass;
    }

    @Override
    public KEY_TYPE getKey() {
        return key;
    }

    //CheckStyle:OFF

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEnitityKey<?> that = (SimpleEnitityKey<?>) o;

        if (!keyTypeClass.equals(that.keyTypeClass)) return false;
        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        int result = keyTypeClass.hashCode();
        result = 31 * result + key.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SimpleEnitityKey{" +
                "keyTypeClass=" + keyTypeClass +
                ", key=" + key +
                '}';
    }

    //CheckStyle:ON

}
