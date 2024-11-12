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
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.common.utils.ByteUtils;
import com.netty100.cluster.common.utils.StringUtils;
import com.netty100.cluster.consistency.ProtocolMetaData;
import com.netty100.cluster.consistency.cp.CPProtocol;
import com.netty100.cluster.consistency.cp.MetadataKey;
import com.netty100.cluster.consistency.entity.ReadRequest;
import com.netty100.cluster.consistency.entity.Response;
import com.netty100.cluster.consistency.entity.WriteRequest;
import com.netty100.cluster.core.distributed.ProtocolManager;
import com.netty100.cluster.core.exception.ErrorCode;
import com.netty100.cluster.naming.consistency.Datum;
import com.netty100.cluster.naming.consistency.RecordListener;
import com.netty100.cluster.naming.constants.Constants;
import com.netty100.cluster.naming.misc.Loggers;
import com.netty100.cluster.naming.pojo.Record;
import com.netty100.cluster.sys.env.EnvUtil;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * In cluster mode, start the Raft protocol.
 *
 * @author yewenhai
 */
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class PersistentServiceProcessor extends BasePersistentServiceProcessor {
    
    private final CPProtocol protocol;

    /**
     * Is there a leader node currently.
     */
    private volatile boolean hasLeader = false;
    
    public PersistentServiceProcessor(ProtocolManager protocolManager) throws Exception {
        this.protocol = protocolManager.getCpProtocol();
    }
    
    @Override
    public void afterConstruct() {
        super.afterConstruct();
        String raftGroup = Constants.NAMING_PERSISTENT_SERVICE_GROUP;
        this.protocol.protocolMetaData().subscribe(raftGroup, MetadataKey.LEADER_META_DATA, o -> {
            if (!(o instanceof ProtocolMetaData.ValueItem)) {
                return;
            }
            Object leader = ((ProtocolMetaData.ValueItem) o).getData();
            hasLeader = StringUtils.isNotBlank(String.valueOf(leader));
            Loggers.RAFT.info("Raft group {} has leader {}", raftGroup, leader);
        });
        this.protocol.addRequestProcessors(Collections.singletonList(this));
        // If you choose to use the new RAFT protocol directly, there will be no compatible logical execution
        if (EnvUtil.getProperty(Constants.NACOS_NAMING_USE_NEW_RAFT_FIRST, Boolean.class, false)) {
            NotifyCenter.registerSubscriber(notifier);
            waitLeader();
            startNotify = true;
        }
    }
    
    private void waitLeader() {
        while (!hasLeader && !hasError) {
            Loggers.RAFT.info("Waiting Jraft leader vote ...");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    @Override
    public void put(String key, Record value) throws CapException {
        final BatchWriteRequest req = new BatchWriteRequest();
        Datum datum = Datum.createDatum(key, value);
        req.append(ByteUtils.toBytes(key), serializer.serialize(datum));
        final WriteRequest request = WriteRequest.newBuilder().setData(ByteString.copyFrom(serializer.serialize(req)))
                .setGroup(Constants.NAMING_PERSISTENT_SERVICE_GROUP).setOperation(Op.Write.desc).build();
        try {
            protocol.write(request);
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
            protocol.write(request);
        } catch (Exception e) {
            throw new CapException(ErrorCode.ProtoSubmitError.getCode(), e.getMessage());
        }
    }
    
    @Override
    public Datum get(String key) throws CapException {
        final List<byte[]> keys = new ArrayList<>(1);
        keys.add(ByteUtils.toBytes(key));
        final ReadRequest req = ReadRequest.newBuilder().setGroup(Constants.NAMING_PERSISTENT_SERVICE_GROUP)
                .setData(ByteString.copyFrom(serializer.serialize(keys))).build();
        try {
            Response resp = protocol.getData(req);
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
        return hasLeader && !hasError;
    }
    
    @Override
    public Optional<String> getErrorMsg() {
        String errorMsg;
        if (hasLeader && hasError) {
            errorMsg = "The raft peer is in error: " + jRaftErrorMsg;
        } else if (hasLeader && !hasError) {
            errorMsg = null;
        } else if (!hasLeader && hasError) {
            errorMsg = "Could not find leader! And the raft peer is in error: " + jRaftErrorMsg;
        } else {
            errorMsg = "Could not find leader!";
        }
        return Optional.ofNullable(errorMsg);
    }
}
