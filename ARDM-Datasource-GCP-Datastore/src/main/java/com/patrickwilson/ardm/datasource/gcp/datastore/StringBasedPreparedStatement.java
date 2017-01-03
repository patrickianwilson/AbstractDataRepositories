package com.patrickwilson.ardm.datasource.gcp.datastore;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by pwilson on 12/28/16.
 */
public class StringBasedPreparedStatement {
    private String query;
    private Map<String, Object> parameters = new HashMap<>();

    public StringBasedPreparedStatement(String query, Map<String, Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String name, Object value) {
        this.parameters.put(name, value);
    }
}