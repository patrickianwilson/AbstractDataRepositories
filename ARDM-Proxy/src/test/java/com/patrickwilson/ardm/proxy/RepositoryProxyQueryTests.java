package com.patrickwilson.ardm.proxy;

import com.google.common.collect.Lists;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.api.repository.CRUDRepository;
import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.QueryPage;
import com.patrickwilson.ardm.proxy.query.QueryResult;
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

    private final DataSourceAdaptor mockDataSource = createMock(DataSourceAdaptor.class);

    @Test
    public void doListReturnType() throws Exception {

        MyDataRepository underTest = new RepositoryProvider().bind(MyDataRepository.class).to(mockDataSource);

        final QueryData data = BeanBuilder.newInstance(QueryData.class)
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

        List<MyEntity> result = underTest.findByEntityName();

        Assert.assertThat(result, equalTo(getExpectedResult()));

    }


    @Test
    public void doEntityReturnType() {

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

    /**
     * used for testing.
     */
    @Repository(MyEntity.class)
    public interface MyDataRepository extends CRUDRepository<MyEntity> {

        List<MyEntity> findByEntityName();

        MyEntity findBySecondProperty();

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
    }


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
    }


}
