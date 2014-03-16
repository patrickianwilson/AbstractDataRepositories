package com.patrickwilson.ardm.datasource.api.query.transform;

import com.patrickwilson.ardm.datasource.api.query.QueryData;

/**
 * Implementors can transform a generic query data object into a native query for a target data source.
 * User: pwilson
 *
 * @param <NATIVE_FORMAT> the native format for the query.
 */
public interface QueryLogicTreeTransformer<NATIVE_FORMAT> {

    /**
     * Transform the generic query data into a native format that is acceptible for the target data source.
     * @param data
     * @return
     */
    NativeQueryWrapper<NATIVE_FORMAT> transform(QueryData data);

}
