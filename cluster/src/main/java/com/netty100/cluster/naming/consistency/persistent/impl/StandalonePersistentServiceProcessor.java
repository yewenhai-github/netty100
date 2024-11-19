/*
 *  Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.netty100.cluster.naming.consistency.persistent.impl;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.common.utils.ByteUtils;
import com.netty100.cluster.consistency.entity.ReadRequest;
import com.netty100.cluster.consistency.entity.Response;
import com.netty100.cluster.consistency.entity.WriteRequest;
import com.netty100.cluster.core.exception.ErrorCode;
import com.netty100.cluster.naming.consistency.Datum;
import com.netty100.cluster.naming.consistency.RecordListener;
import com.netty100.cluster.naming.constants.Constants;
import com.netty100.cluster.naming.pojo.Record;
import com.google.protobuf.ByteString;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Persistent service manipulation layer in stand-alone mode.
 *
 * @author why
 */
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class StandalonePersistentServiceProcessor extends BasePersistentServiceProcessor {
    
    public StandalonePersistentServiceProcessor() throws Exception {
    }
    
    @Override
    public void put(String key, Record value) throws CapException {
        final BatchWriteRequest req = new BatchWriteRequest();
        Datum datum = Datum.createDatum(key, value);
        req.append(ByteUtils.toBytes(key), serializer.serialize(datum));
        final WriteRequest request = WriteRequest.newBuilder().setData(ByteString.copyFrom(serializer.serialize(req)))
                .setGroup(Constants.NAMING_PERSISTENT_SERVICE_GROUP).setOperation(Op.Write.desc).build();
        try {
            onApply(request);
        } catch (Exception e) {
            throw new CapException(ErrorCode.ProtoSubmitError.getCode(), e.getMessage());
        }
    }
    
    @Override
    public void remove(String key) throws CapException {
        final BatchWriteRequest req = new BatchWriteRequest();
        req.append(ByteUtils.toBytes(key), ByteUtils.EMPTY);
        final WriteRequest request = WriteRequest.newBuilder().setData(ByteString.copyFrom(serializer.serialize(req)))
                .setGroup(Constants.NAMING_PERSISTENT_SERVICE_GROUP).setOperation(Op.Delete.desc).build();
        try {
            onApply(request);
        } catch (Exception e) {
            throw new CapException(ErrorCode.ProtoSubmitError.getCode(), e.getMessage());
        }
    }
    
    @Override
    public Datum get(String key) throws CapException {
        final List<byte[]> keys = Collections.singletonList(ByteUtils.toBytes(key));
        final ReadRequest req = ReadRequest.newBuilder().setGroup(Constants.NAMING_PERSISTENT_SERVICE_GROUP)
                .setData(ByteString.copyFrom(serializer.serialize(keys))).build();
        try {
            final Response resp = onRequest(req);
            if (resp.getSuccess()) {
                BatchReadResponse response = serializer
                        .deserialize(resp.getData().toByteArray(), BatchReadResponse.class);
                final List<byte[]> rValues = response.getValues();
                return rValues.isEmpty() ? null : serializer.deserialize(rValues.get(0), getDatumTypeFromKey(key));
            }
            throw new CapException(ErrorCode.ProtoReadError.getCode(), resp.getErrMsg());
        } catch (Throwable e) {
            throw new CapException(ErrorCode.ProtoReadError.getCode(), e.getMessage());
        }
    }
    
    @Override
    public void listen(String key, RecordListener listener) throws CapException {
        notifier.registerListener(key, listener);
        if (startNotify) {
            notifierDatumIfAbsent(key, listener);
        }
    }
    
    @Override
    public void unListen(String key, RecordListener listener) throws CapException {
        notifier.deregisterListener(key, listener);
    }
    
    @Override
    public boolean isAvailable() {
        return !hasError;
    }
    
    @Override
    public Optional<String> getErrorMsg() {
        String errorMsg;
        if (hasError) {
            errorMsg = "The raft peer is in error: " + jRaftErrorMsg;
        } else {
            errorMsg = null;
        }
        return Optional.ofNullable(errorMsg);
    }
}
