package com.patrickwilson.utils.retvalue;

/**
 * Created by pwilson on 12/28/16.
 * @param <A> the first type in the tuple.
 * @param <B> the second type in the tuple.
 */
public class Tuple<A, B> {
    private A first;
    private B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public B getSecond() {
        return second;
    }

    public A getFirst() {
        return first;
    }
}
