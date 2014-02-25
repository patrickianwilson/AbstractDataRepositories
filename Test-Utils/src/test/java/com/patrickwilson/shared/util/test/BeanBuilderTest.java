package com.patrickwilson.shared.util.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

/**
 * some simple unit tests for the bean builder reflective utility class.
 * User: pwilson
 */
public class BeanBuilderTest {


    protected static final int SAMPLE_INT = 232;

    @Test
    public void testObjectClone() throws Exception {

        Complex input = new Complex();
        input.setM("test");
        input.setMyInt(SAMPLE_INT);

        Complex actual = BeanBuilder.newInstance(Complex.class)
                .fromObject(input)
                .with("simple", "testS")
                .build();

        Complex expected = new Complex();

        expected.setMyInt(SAMPLE_INT);
        expected.setM("test");
        expected.setSimple("testS");
        expected.setMyObj(actual.getMyObj());

        Assert.assertThat(expected, equalTo(actual));


    }

    @Test
    public void testInnerObject() throws Exception {

        NestedObject single = new NestedObject();

        single.setA("test");
        single.setB(-1);

        RootObject expectedRoot = new RootObject();
        expectedRoot.setSingle(single);

        NestedObject second = new NestedObject();
        second.setA("test");


        List<NestedObject> nestedList = new ArrayList<NestedObject>();
        expectedRoot.setMultiple(nestedList);

        nestedList.add(single);
        nestedList.add(second);

        RootObject actualRoot = BeanBuilder.newInstance(RootObject.class)
                .withSubObject("single", NestedObject.class, new BeanBuilder.SubObjectTemplate<NestedObject>() {
                    @Override
                    public void defineObject(BeanBuilder<NestedObject> builder) throws Exception {
                       builder.with("a", "test")
                              .with("b", -1);

                    }
                })
                .withAdditionalEntry("multiple", ArrayList.class, NestedObject.class, new BeanBuilder.SubObjectTemplate<NestedObject>() {
                    @Override
                    public void defineObject(BeanBuilder<NestedObject> builder) throws Exception {
                        builder .with("a", "test")
                                .with("b", -1);
                    }
                })
                .withAdditionalEntry("multiple", ArrayList.class, NestedObject.class, new BeanBuilder.SubObjectTemplate<NestedObject>() {
                    @Override
                    public void defineObject(BeanBuilder<NestedObject> builder) throws Exception {
                        builder.with("a", "test");
                    }
                })
                .build();

        Assert.assertThat(actualRoot, is(equalTo(expectedRoot)));

    }

    /**
     * sample complex class.
     */
    public static class Complex {
        private String simple;
        private String m;


        private int myInt;

        private Object myObj = new Object();


        public String getSimple() {
            return simple;
        }

        public void setSimple(String simple) {
            this.simple = simple;
        }

        public String getM() {
            return m;
        }

        public void setM(String m) {
            this.m = m;
        }

        public int getMyInt() {
            return myInt;
        }

        public void setMyInt(int myInt) {
            this.myInt = myInt;
        }

        public Object getMyObj() {
            return myObj;
        }

        public void setMyObj(Object myObj) {
            this.myObj = myObj;
        }

        //CheckStyle:OFF
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Complex)) return false;

            Complex complex = (Complex) o;

            if (myInt != complex.myInt) return false;
            if (m != null ? !m.equals(complex.m) : complex.m != null) return false;
            if (myObj != null ? !myObj.equals(complex.myObj) : complex.myObj != null) return false;
            if (simple != null ? !simple.equals(complex.simple) : complex.simple != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = simple != null ? simple.hashCode() : 0;
            result = 31 * result + (m != null ? m.hashCode() : 0);
            result = 31 * result + myInt;
            result = 31 * result + (myObj != null ? myObj.hashCode() : 0);
            return result;
        }

        //CheckStyle:ON
    }


    /**
     * used for testing.
     */
    public static class RootObject {

        private NestedObject single;

        private List<NestedObject> multiple;

        public NestedObject getSingle() {
            return single;
        }

        public void setSingle(NestedObject single) {
            this.single = single;
        }

        public List<NestedObject> getMultiple() {
            return multiple;
        }

        public void setMultiple(List<NestedObject> multiple) {
            this.multiple = multiple;
        }

        //CheckStyle:OFF
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RootObject)) return false;

            RootObject that = (RootObject) o;

            if (multiple != null ? !multiple.equals(that.multiple) : that.multiple != null) return false;
            if (single != null ? !single.equals(that.single) : that.single != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = single != null ? single.hashCode() : 0;
            result = 31 * result + (multiple != null ? multiple.hashCode() : 0);
            return result;
        }
        //CheckStyle:ON
    }


    /**
     * used for testing.
     */
    public static class NestedObject {

        private String a;
        private int b;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        //CheckStyle:OFF
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NestedObject)) return false;

            NestedObject that = (NestedObject) o;

            if (b != that.b) return false;
            if (a != null ? !a.equals(that.a) : that.a != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + b;
            return result;
        }
        //CheckStyle:ON
    }
}
