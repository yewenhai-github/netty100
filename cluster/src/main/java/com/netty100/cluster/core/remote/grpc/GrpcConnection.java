/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netty100.cluster.core.remote.grpc;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.grpc.auto.Payload;
import com.netty100.cluster.api.remote.DefaultRequestFuture;
import com.netty100.cluster.api.remote.RequestCallBack;
import com.netty100.cluster.api.remote.RequestFuture;
import com.netty100.cluster.api.remote.request.Request;
import com.netty100.cluster.api.remote.response.Response;
import com.netty100.cluster.common.remote.client.grpc.GrpcUtils;
import com.netty100.cluster.common.remote.exception.ConnectionAlreadyClosedException;
import com.netty100.cluster.core.remote.Connection;
import com.netty100.cluster.core.remote.ConnectionMeta;
import com.netty100.cluster.core.remote.RpcAckCallbackSynchronizer;
import com.netty100.cluster.core.utils.Loggers;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

/**
 * grpc connection.
 *
 * @author yewenhai
 * @version $Id: GrpcConnection.java, v 0.1 2020年07月13日 7:26 PM liuzunfei Exp $
 */
public class GrpcConnection extends Connection {
    
    private StreamObserver streamObserver;
    
    private Channel channel;
    
    public GrpcConnection(ConnectionMeta metaInfo, StreamObserver streamObserver, Channel channel) {
        super(metaInfo);
        this.streamObserver = streamObserver;
        this.channel = channel;
    }
    
    private void sendRequestNoAck(Request request) throws CapException {
        try {
            //StreamObserver#onNext() is not thread-safe,synchronized is required to avoid direct memory leak.
            synchronized (streamObserver) {
                
                Payload payload = GrpcUtils.convert(request);
                traceIfNecessary(payload);
                streamObserver.onNext(payload);
            }
        } catch (Exception e) {
            if (e instanceof StatusRuntimeException) {
                throw new ConnectionAlreadyClosedException(e);
            }
            throw e;
        }
    }
    
    private void traceIfNecessary(Payload payload) {
        String connectionId = null;
        if (this.isTraced()) {
            try {
                connectionId = getMetaInfo().getConnectionId();
                Loggers.REMOTE_DIGEST.info("[{}]Send request to client ,payload={}", connectionId,
                        payload.toByteString().toStringUtf8());
            } catch (Throwable throwable) {
                Loggers.REMOTE_DIGEST
                        .warn("[{}]Send request to client trace error, ,error={}", connectionId, throwable);
            }
        }
    }
    
    private DefaultRequestFuture sendRequestInner(Request request, RequestCallBack callBack) throws CapException {
        final String requestId = String.valueOf(PushAckIdGenerator.getNextId());
        request.setRequestId(requestId);
        
        DefaultRequestFuture defaultPushFuture = new DefaultRequestFuture(getMetaInfo().getConnectionId(), requestId,
                callBack, () -> RpcAckCallbackSynchronizer.clearFuture(getMetaInfo().getConnectionId(), requestId));
        
        RpcAckCallbackSynchronizer.syncCallback(getMetaInfo().getConnectionId(), requestId, defaultPushFuture);
        sendRequestNoAck(request);
        return defaultPushFuture;
    }
    
    @Override
    public Response request(Request request, long timeoutMills) throws CapException {
        DefaultRequestFuture pushFuture = sendRequestInner(request, null);
        try {
            return pushFuture.get(timeoutMills);
        } catch (Exception e) {
            throw new CapException(CapException.SERVER_ERROR, e);
        } finally {
            RpcAckCallbackSynchronizer.clearFuture(getMetaInfo().getConnectionId(), pushFuture.getRequestId());
        }
    }
    
    @Override
    public RequestFuture requestFuture(Request request) throws CapException {
        return sendRequestInner(request, null);
    }
    
    @Override
    public void asyncRequest(Request request, RequestCallBack requestCallBack) throws CapException {
        sendRequestInner(request, requestCallBack);
    }
    
    @Override
    public void close() {
        String connectionId = null;
        
        try {
            connectionId = getMetaInfo().getConnectionId();
            
            if (isTraced()) {
                Loggers.REMOTE_DIGEST.warn("[{}] try to close connection ", connectionId);
            }
            
            closeBiStream();
            channel.close();
            
        } catch (Exception e) {
            Loggers.REMOTE_DIGEST.warn("[{}] connection  close exception  : {}", connectionId, e);
        }
    }
    
    private void closeBiStream() {
        if (streamObserver instanceof ServerCallStreamObserver) {
            ServerCallStreamObserver serverCallStreamObserver = ((ServerCallStreamObserver) streamObserver);
            if (!serverCallStreamObserver.isCancelled()) {
                serverCallStreamObserver.onCompleted();
            }
        }
    }
    
    @Override
    public boolean isConnected() {
        return channel != null && channel.isOpen() && channel.isActive();
    }
}
