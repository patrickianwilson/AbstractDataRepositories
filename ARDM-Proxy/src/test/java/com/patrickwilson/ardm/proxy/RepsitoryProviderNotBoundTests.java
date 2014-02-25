package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryResult;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import org.junit.Test;

/**
 * Unit tests handling the situation where a repository was generated for a
 * User: pwilson
 */
public class RepsitoryProviderNotBoundTests extends BaseJMockTest {

     final DataSourceAdaptor mockDataSource = createMock(DataSourceAdaptor.class);

    @Test
    public void testGenericsApi() {

        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository mockRepository = provider.bind(MyDataRepository.class).to(mockDataSource);


    }


   @Repository
    public static interface MyDataRepository {

    }


}
