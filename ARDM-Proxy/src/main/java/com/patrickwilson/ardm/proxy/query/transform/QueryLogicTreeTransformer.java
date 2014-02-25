package com.patrickwilson.ardm.proxy.query.transform;

import com.patrickwilson.ardm.proxy.query.QueryData;
import com.patrickwilson.ardm.proxy.query.transform.NativeQueryWrapper;

/**
 * Implementors can transform a generic query data object into a native query for a target data source.
 * User: pwilson
 */
public interface QueryLogicTreeTransformer<NATIVE_FORMAT> {

    /**
     * Transform the generic query data into a native format that is acceptible for the target data source.
     * @param data
     * @return
     */
    NativeQueryWrapper<NATIVE_FORMAT> transform(QueryData data);

}
