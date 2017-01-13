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
 * A typesafe key interface.
 * User: pwilson
 * @param <KEY_TYPE> the Type of object representing the key.
 */
public interface EntityKey<KEY_TYPE> extends Comparable<SimpleEntityKey> {

    KEY_TYPE getKey();

    Class<KEY_TYPE> getKeyClass();

    /**
     * A way to tell whether this key complete (in that there is a unique id) or incomplete (datasource will autogenerate).
     * @return
     */
    boolean isPopulated();

}
