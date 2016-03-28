package com.patrickwilson.ardm.proxy;

import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * test scenarios for reposoitory validation logic.
 * User: pwilson
 */
public class RepositoryCorrectnessTests extends BaseJMockTest {

    private final ScanableDatasourceAdaptor mockDataSource = createMock(ScanableDatasourceAdaptor.class);

    @Test(expected = RepositoryDeclarationException.class)
    public void testInvalidFindAllReturnType() {

        RepositoryProvider provider = new RepositoryProvider();

        MyDataRepository underTest = provider.bind(MyDataRepository.class).to(mockDataSource);

        final MyEntity entity = new MyEntity();

        addExpectations(new Expectations() { {
            oneOf(mockDataSource).findAll(MyEntity.class);
            will(returnValue(buildQueryResult(entity)));
        } });

        underTest.findAll();

    }

    @Test
    public void testValidFindAllReturnType() {

        RepositoryProvider provider = new RepositoryProvider();

        MyValidRepository underTest = provider.bind(MyValidRepository.class).to(mockDataSource);

        final MyEntity entity = new MyEntity();

        addExpectations(new Expectations() { {
            oneOf(mockDataSource).findAll(MyEntity.class);
            will(returnValue(buildQueryResult(entity)));
        } });


        final List<MyEntity> saved = underTest.findAll();

        Assert.assertEquals(saved.size(), 1);
        Assert.assertEquals(saved.get(0), entity);

    }

    private QueryResult<MyEntity> buildQueryResult(MyEntity... entities) {

        QueryResult<MyEntity> entityQueryResult = new QueryResult<>();

        entityQueryResult.setResults(Lists.newArrayList(entities));
        entityQueryResult.setStartIndex(0);
        entityQueryResult.setNumResults(entities.length);

        return entityQueryResult;
    }

    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity, String> {

         String findAll();
        
    }


    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyValidRepository extends CRUDRepository<MyEntity, String> {

        List<MyEntity> findAll();

    }





    /**
     * used for testing.
     */
    public static class MyEntity {

    }

}
