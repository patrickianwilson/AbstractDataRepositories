package com.patrickwilson.ardm.datasource.memory;
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
