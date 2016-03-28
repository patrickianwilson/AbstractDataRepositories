package com.patrickwilson.ardm.proxy;

import org.junit.Assert;
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
