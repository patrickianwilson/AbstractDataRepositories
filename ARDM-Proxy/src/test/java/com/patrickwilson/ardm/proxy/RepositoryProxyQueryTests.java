package com.patrickwilson.ardm.proxy;

import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Query;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.datasource.api.QueriableDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.query.QueryData;
import com.patrickwilson.ardm.datasource.api.query.QueryLogicTree;
import com.patrickwilson.ardm.datasource.api.query.QueryPage;
import com.patrickwilson.ardm.datasource.api.query.QueryResult;
import com.patrickwilson.ardm.datasource.api.query.ValueLogicTreeNode;
import com.patrickwilson.shared.util.test.BaseJMockTest;
import com.patrickwilson.shared.util.test.BeanBuilder;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import static org.hamcrest.CoreMatchers.*;

/**
 * Unit tests testing the query autowiring.
 * User: pwilson
 */
public class RepositoryProxyQueryTests extends BaseJMockTest {

    private final QueriableDatasourceAdaptor mockDataSource = createMock(QueriableDatasourceAdaptor.class);

    @Test
    public void doListReturnType() throws Exception {

        MyDataRepository underTest = new RepositoryProvider().bind(MyDataRepository.class).to(mockDataSource);
        final String param = "argument";

        final QueryData data = BeanBuilder.newInstance(QueryData.class)
                .with("page", getMultiEntityQueryPage())
                .with("criteria", getSingleNodeQueryTree("entityname"))
                .with("parameters", new Object[] {param })
                .build();



        final QueryResult<MyEntity> adaptorResponse = new QueryResult<>();

        adaptorResponse.setResults(getExpectedResult());
        adaptorResponse.setNumResults(2);

        addExpectations(new Expectations() {
            {
                oneOf(mockDataSource).findByCriteria(with(data));
                will(returnValue(adaptorResponse));
            }
        });

        List<MyEntity> result = underTest.findByEntityName(param);

        Assert.assertThat(result, equalTo(getExpectedResult()));

    }


    @Test
    public void doEntityReturnType() throws Exception {
        MyDataRepository underTest = new RepositoryProvider().bind(MyDataRepository.class).to(mockDataSource);

        int param = 1;
        final QueryData data = BeanBuilder.newInstance(QueryData.class)
                .with("page", getSingleEntityRequestPage())
                .with("criteria", getSingleNodeQueryTree("secondproperty"))
                .with("parameters", new Object[] {param })
                .build();

        final QueryResult<MyEntity> adaptorResponse = new QueryResult<>();

        adaptorResponse.setResults(getExpectedResult());
        adaptorResponse.setNumResults(2);

        addExpectations(new Expectations() {
            {
                oneOf(mockDataSource).findByCriteria(with(data));
                will(returnValue(adaptorResponse));
            }
        });

        MyEntity result = underTest.findBySecondProperty(param);

        Assert.assertThat(result, equalTo(getFirstEntity()));
    }

    private List<MyEntity> getExpectedResult() throws Exception {
        return Lists.newArrayList(getFirstEntity(), getSecondEntity());
    }


    private MyEntity getFirstEntity() throws Exception {
        return BeanBuilder.newInstance(MyEntity.class)
                .with("entityName", "my entity")
                .with("secondProperty", "another")
                .build();
    }

    private MyEntity getSecondEntity() throws Exception {
        return BeanBuilder.newInstance(MyEntity.class)
                .fromObject(getFirstEntity())
                .with("entityName", "another entity")
                .build();
    }

    private QueryPage getSingleEntityRequestPage() {
        QueryPage page = new QueryPage();
        page.setStartIndex(0);
        page.setNumberOfResults(1);

        return page;
    }

    private QueryPage getMultiEntityQueryPage() {
        QueryPage page = getSingleEntityRequestPage();
        page.setNumberOfResults(QueryPage.NOT_SET);
        return page;
    }
    
    private QueryLogicTree getSingleNodeQueryTree(String propertyName) {
        QueryLogicTree result = new QueryLogicTree();

        ValueLogicTreeNode node = new ValueLogicTreeNode();
        node.setValueArgIndex(0);
        node.setColumnName(propertyName);
        result.setRootCriteria(node);


        return result;
    }

    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity> {

        @Query
        List<MyEntity> findByEntityName(String entityName);

        @Query
        MyEntity findBySecondProperty(int arg);

    }

    /**
     * used for testing.
     */
    public static class MyEntity {

        private MyEntityKey id;

        private String entityName;

        private String secondProperty;

        public MyEntityKey getId() {
            return id;
        }

        public void setId(MyEntityKey id) {
            this.id = id;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public String getSecondProperty() {
            return secondProperty;
        }

        public void setSecondProperty(String secondProperty) {
            this.secondProperty = secondProperty;
        }


        //CheckStyle:OFF
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MyEntity)) return false;

            MyEntity myEntity = (MyEntity) o;

            if (entityName != null ? !entityName.equals(myEntity.entityName) : myEntity.entityName != null)
                return false;
            if (id != null ? !id.equals(myEntity.id) : myEntity.id != null) return false;
            if (secondProperty != null ? !secondProperty.equals(myEntity.secondProperty) : myEntity.secondProperty != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
            result = 31 * result + (secondProperty != null ? secondProperty.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MyEntity{" +
                    "id=" + id +
                    ", entityName='" + entityName + '\'' +
                    ", secondProperty='" + secondProperty + '\'' +
                    '}';
        }
        //CheckStyle:ON

    }


    /**
     * for unit testing.
     */
    public static class MyEntityKey extends SimpleEnitityKey<String> {

        private String key;

        public MyEntityKey(String key) {
            super(String.class);

        }

        public MyEntityKey() {
            super(String.class);
        }

        @Override
        public String getKey() {
            return key;
        }

        //CheckStyle:OFF

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MyEntityKey)) return false;
            if (!super.equals(o)) return false;

            MyEntityKey that = (MyEntityKey) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MyEntityKey{" +
                    "key='" + key + '\'' +
                    '}';
        }

        //CheckStyle:ON

    }


}
