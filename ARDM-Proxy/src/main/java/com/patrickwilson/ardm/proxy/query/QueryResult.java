package com.patrickwilson.ardm.proxy.query;


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
}
