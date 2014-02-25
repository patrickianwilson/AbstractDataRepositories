package com.patrickwilson.ardm.proxy.query.transform;

/**
 * Allow a query object to be wrapped in some metadata for convenience.
 * User: pwilson
 *
 * @param <NATIVE_FORMAT> a native query format (ie: javax.jpa.Query object)
 */
public class NativeQueryWrapper<NATIVE_FORMAT> {

    public static final int NOT_DEFINED = -1;

    private NATIVE_FORMAT nativeQuery;
    private int firstRecord = NOT_DEFINED;
    private int numRecords = NOT_DEFINED;


    public NATIVE_FORMAT getNativeQuery() {
        return nativeQuery;
    }

    public void setNativeQuery(NATIVE_FORMAT nativeQuery) {
        this.nativeQuery = nativeQuery;
    }

    public int getFirstRecord() {
        return firstRecord;
    }

    public void setFirstRecord(int firstRecord) {
        this.firstRecord = firstRecord;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public void setNumRecords(int numRecords) {
        this.numRecords = numRecords;
    }
}
