package com.patrickwilson.ardm.proxy.query;


import com.google.gson.JsonObject;

import java.util.List;

/**
 * A wrapper to hold the result that comes back in native format.
 * User: pwilson
 */
public class QueryResult {

    private List<JsonObject> results;
    private int startIndex;
    private int numResults;

    public List<JsonObject> getResults() {
        return results;
    }

    public void setResults(List<JsonObject> results) {
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
