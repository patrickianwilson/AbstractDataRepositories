package com.patrickwilson.shared.util.test;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * This is a base class for JMock tests.
 */
@RunWith(JMock.class)
@Ignore
public class BaseJMockTest {


    /**
     * the shared and pre configured mockery.
     */
    private final Mockery context = new JUnit4Mockery() { {
        setThreadingPolicy(new Synchroniser());
    } };

    /**
     * setup the base jmock test.
     */
    public BaseJMockTest() {
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }

    /**
     * create an un-named mock object.
     * @param clazz  type to mock

     * @param <T> inferred type
     * @return  a mock object of type T.
     */
    public <T> T createMock(Class<T> clazz) {

        return context.mock(clazz);
    }

    /**
     * create an un-named mock object.
     * @param clazz  type to mock
     * @param name A name for the mock object.
     * @param <T> inferred type
     * @return  a mock object of type T.
     */

    public <T> T createMock(Class<T> clazz, String name) {

        return context.mock(clazz, name);
    }

    /**
     * Add expectations to the current mockery.
     * @param expectations a set of expectations to add to the mockery.
     */
    public void addExpectations(ExpectationBuilder expectations) {
          this.context.checking(expectations);
    }



    /**
     * get the shared mockery object for this test.
     * @return   a preconfigured mockery instance
     */
    protected Mockery getContext() {
        return context;
    }
}
