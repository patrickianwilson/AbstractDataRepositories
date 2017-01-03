package com.patrickwilson.utils.retvalue;

/**
 * Created by pwilson on 12/28/16.
 */
public class ReturnValue {

    public static final <A, B> Tuple<A, B> returnTwo(A first, B second) {
        return new Tuple<>(first, second);
    }

    public static final <A, B, C> Triple<A, B, C> returnThree(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }
}
