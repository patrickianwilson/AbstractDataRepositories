package com.patrickwilson.utils.retvalue;

/**
 * Created by pwilson on 12/28/16.
 * @param <A> first type in the triple.
 * @param <B> second type in the triple.
 * @param <C> third type in the triple.
 *
 */
public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }
}
