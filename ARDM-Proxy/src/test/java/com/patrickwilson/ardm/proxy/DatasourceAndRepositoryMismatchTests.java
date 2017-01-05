package com.patrickwilson.ardm.proxy;
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
import org.junit.Ignore;
import org.junit.Test;
import com.patrickwilson.ardm.api.annotation.Attribute;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.shared.util.test.BaseJMockTest;


/**
 * This test is intended to try to verify the correct behaviour when a repository method is
 * added to a repo but bound to a datasource that does not support the functionality.
 */
@Ignore
public class DatasourceAndRepositoryMismatchTests extends BaseJMockTest {


    private final CRUDDatasourceAdaptor mockDataSource = createMock(CRUDDatasourceAdaptor.class);

    @Test
    public void makeUnsupportedCall() {
        Assert.fail("test not implemented yet.");
    }
    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity, String> {


    }

    /**
     * used for testing.
     */
    public static class MyEntity {

        @Attribute
        private String id;

        @Attribute
        private String attrValue;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAttrValue() {
            return attrValue;
        }

        public void setAttrValue(String attrValue) {
            this.attrValue = attrValue;
        }
    }
}
