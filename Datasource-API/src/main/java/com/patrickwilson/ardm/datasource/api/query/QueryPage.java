package com.patrickwilson.ardm.datasource.api.query;
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
/**
 * A data class representing a query page (ie LIMIT) style request.
 * User: pwilson
 */
public class QueryPage {

    public static final int NOT_SET = -1;

    private int startIndex;
    private int numberOfResults =  NOT_SET;

    public QueryPage() {
    }

    public QueryPage(int startIndex, int numberOfResults) {
        this.startIndex = startIndex;
        this.numberOfResults = numberOfResults;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(int numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    //CheckStyle:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryPage)) return false;

        QueryPage queryPage = (QueryPage) o;

        if (numberOfResults != queryPage.numberOfResults) return false;
        if (startIndex != queryPage.startIndex) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startIndex;
        result = 31 * result + numberOfResults;
        return result;
    }

    @Override
    public String toString() {
        return "QueryPage{" +
                "startIndex=" + startIndex +
                ", numberOfResults=" + numberOfResults +
                '}';
    }

    //CheckStyle:ON
}
