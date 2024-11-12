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

package com.netty100.cluster.core.remote;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.remote.AbstractRequestCallBack;
import com.netty100.cluster.api.remote.request.ServerRequest;
import com.netty100.cluster.api.remote.PushCallBack;
import com.netty100.cluster.api.remote.response.Response;
import com.netty100.cluster.common.remote.exception.ConnectionAlreadyClosedException;
import com.netty100.cluster.core.utils.Loggers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

/**
 * push response  to clients.
 *
 * @author yewenhai
 * @version $Id: PushService.java, v 0.1 2020年07月20日 1:12 PM liuzunfei Exp $
 */
@Service
public class RpcPushService {
    
    @Autowired
    private ConnectionManager connectionManager;
    
    /**
     * push response with no ack.
     *
     * @param connectionId    connectionId.
     * @param request         request.
     * @param requestCallBack requestCallBack.
     */
    public void pushWithCallback(String connectionId, ServerRequest request, PushCallBack requestCallBack,
                                 Executor executor) {
        Connection connection = connectionManager.getConnection(connectionId);
        if (connection != null) {
            try {
                connection.asyncRequest(request, new AbstractRequestCallBack(requestCallBack.getTimeout()) {
                    
                    @Override
                    public Executor getExecutor() {
                        return executor;
                    }
                    
                    @Override
                    public void onResponse(Response response) {
                        if (response.isSuccess()) {
                            requestCallBack.onSuccess();
                        } else {
                            requestCallBack.onFail(new CapException(response.getErrorCode(), response.getMessage()));
                        }
                    }
                    
                    @Override
                    public void onException(Throwable e) {
                        requestCallBack.onFail(e);
                    }
                });
            } catch (ConnectionAlreadyClosedException e) {
                connectionManager.unregister(connectionId);
                requestCallBack.onSuccess();
            } catch (Exception e) {
                Loggers.REMOTE_DIGEST
                        .error("error to send push response to connectionId ={},push response={}", connectionId,
                                request, e);
                requestCallBack.onFail(e);
            }
        } else {
            requestCallBack.onSuccess();
        }
    }
    
    /**
     * push response with no ack.
     *
     * @param connectionId connectionId.
     * @param request      request.
     */
    public void pushWithoutAck(String connectionId, ServerRequest request) {
        Connection connection = connectionManager.getConnection(connectionId);
        if (connection != null) {
            try {
                connection.request(request, 3000L);
            } catch (ConnectionAlreadyClosedException e) {
                connectionManager.unregister(connectionId);
            } catch (Exception e) {
                Loggers.REMOTE_DIGEST
                        .error("error to send push response to connectionId ={},push response={}", connectionId,
                                request, e);
            }
        }
    }
    
}
