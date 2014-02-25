package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import org.junit.Test;

/**
 * Unit tests handling the situation where a repository was generated.
 * User: pwilson
 */
public class RepsitoryProviderNotBoundTests extends BaseJMockTest {

     private final DataSourceAdaptor mockDataSource = createMock(DataSourceAdaptor.class);

    @Test
    public void testGenericsApi() {

        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository mockRepository = provider.bind(MyDataRepository.class).to(mockDataSource);


    }


    /**
     * used for testing.
     */
   @Repository
    public interface MyDataRepository {

    }


}
