package com.patrickwilson.ardm.proxy;

import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests handling the situation where a repository was generated.
 * User: pwilson
 */
public class RepsitoryProviderProxyPassthroughTests extends BaseJMockTest {

     private final CRUDDatasourceAdaptor mockDataSource = createMock(CRUDDatasourceAdaptor.class);

    @Test
    public void testGenericsApi() {

        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository underTest = provider.bind(MyDataRepository.class).to(mockDataSource);

        final MyEntity entity = new MyEntity();

        addExpectations(new Expectations() { {
            oneOf(mockDataSource).save(entity, MyEntity.class);
                will(returnValue(entity));
        } });


        final MyEntity saved = underTest.save(entity);
        
        Assert.assertTrue(entity.equals(saved));



    }


    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity> {


    }

    /**
     * used for testing.
     */
    public static class MyEntity {

    }


}
