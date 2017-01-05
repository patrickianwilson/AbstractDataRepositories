package com.patrickwilson.ardm.api.repository;

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
import java.util.List;

/**
 * A wrapper to hold the result that comes back in native format.
 * User: pwilson
 * @param <ENTITY_TYPE> the type of the underlying entity
 */
public class QueryResult<ENTITY_TYPE> {

    private List<ENTITY_TYPE> results;
    private int startIndex;
    private int numResults;

    public List<ENTITY_TYPE> getResults() {
        return results;
    }

    public void setResults(List<ENTITY_TYPE> results) {
        this.results = results;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    //CheckStyle:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryResult)) return false;

        QueryResult that = (QueryResult) o;

        if (numResults != that.numResults) return false;
        if (startIndex != that.startIndex) return false;
        if (results != null ? !results.equals(that.results) : that.results != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = results != null ? results.hashCode() : 0;
        result = 31 * result + startIndex;
        result = 31 * result + numResults;
        return result;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "results=" + results +
                ", startIndex=" + startIndex +
                ", numResults=" + numResults +
                '}';
    }

    //CheckStyle:ON
}
