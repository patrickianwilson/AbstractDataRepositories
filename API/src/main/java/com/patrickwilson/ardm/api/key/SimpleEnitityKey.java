package com.patrickwilson.ardm.api.key;
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