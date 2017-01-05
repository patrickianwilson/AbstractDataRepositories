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
import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.api.repository.ScannableRepository;
import com.patrickwilson.ardm.datasource.api.ScanableDatasourceAdaptor;
import com.patrickwilson.ardm.api.repository.QueryResult;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

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


        final QueryResult<MyEntity> saved = underTest.findAll();

        Assert.assertEquals(saved.getResults().size(), 1);
        Assert.assertEquals(saved.getResults().get(0), entity);

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
    public interface MyValidRepository extends CRUDRepository<MyEntity, String>, ScannableRepository<MyEntity, String> {



    }





    /**
     * used for testing.
     */
    public static class MyEntity {

    }

}
