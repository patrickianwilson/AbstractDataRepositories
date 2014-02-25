package com.patrickwilson.shared.util.test.exceptions;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * A Custom matcher to verify the correct internal details of an Exception.
 * User: pwilson
 */
public class ExceptionCauseMatcher extends BaseMatcher<Class<? extends Exception>> {

    private Class<? extends Exception> cause;

    public ExceptionCauseMatcher(Class<? extends Exception> cause) {
        this.cause = cause;
    }

    public static ExceptionCauseMatcher withCause(Class<? extends Exception> cause) {
        return new ExceptionCauseMatcher(cause);
    }

    /**
     * Evaluates the matcher for argument item
     * <p/>
     * This method matches against Object, instead of the generic type T. This is
     * because the caller of the Matcher does not know at runtime what the type is
     * (because of type erasure with Java generics). It is down to the implementations
     * to check the correct type.
     *
     * @param item the object against which the matcher is evaluated.
     * @return <code>true</code> if item matches, otherwise <code>false</code>.
     * @see org.hamcrest.BaseMatcher
     */
    @Override
    public boolean matches(Object item) {
        if (!(item instanceof Throwable)) {
            return false;
        }

        Throwable casted = (Throwable) item;

        Throwable inner = findCause(casted);

        return inner != null ? true : false;

    }

    protected Throwable findCause(Throwable wrapper) {

        while (wrapper != null) {

            if (cause.isInstance(wrapper)) {
                return wrapper;
            }

            wrapper = wrapper.getCause(); //recurse
        }

        return null;
    }

    /**
     * Generates a description of the object.  The description may be part of a
     * a description of a larger object of which this is just a component, so it
     * should be worded appropriately.
     *
     * @param description The description to be built or appended to.
     */
    @Override
    public void describeTo(Description description) {
        description
                .appendText("A Throwable with a cause of ")
                .appendText(cause.getSimpleName());
    }

}