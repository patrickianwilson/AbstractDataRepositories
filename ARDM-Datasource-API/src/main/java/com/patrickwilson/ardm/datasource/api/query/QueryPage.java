package com.patrickwilson.ardm.datasource.api.query;

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
