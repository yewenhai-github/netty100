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

package com.netty100.cluster.core.remote.core;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.remote.RemoteConstants;
import com.netty100.cluster.api.remote.request.RequestMeta;
import com.netty100.cluster.api.remote.request.ServerReloadRequest;
import com.netty100.cluster.api.remote.response.ServerReloadResponse;
import com.netty100.cluster.core.remote.ConnectionManager;
import com.netty100.cluster.core.remote.RequestHandler;
import com.netty100.cluster.core.utils.Loggers;
import com.netty100.cluster.core.utils.RemoteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * server reload request handler.
 *
 * @author why
 * @version $Id: ServerReloaderRequestHandler.java, v 0.1 2020年11月09日 4:38 PM liuzunfei Exp $
 */
@Component
public class ServerReloaderRequestHandler extends RequestHandler<ServerReloadRequest, ServerReloadResponse> {
    
    @Autowired
    private ConnectionManager connectionManager;
    
    @Override
    public ServerReloadResponse handle(ServerReloadRequest request, RequestMeta meta) throws CapException {
        ServerReloadResponse response = new ServerReloadResponse();
        Loggers.REMOTE.info("server reload request receive,reload count={},redirectServer={},requestIp={}",
                request.getReloadCount(), request.getReloadServer(), meta.getClientIp());
        int reloadCount = request.getReloadCount();
        Map<String, String> filter = new HashMap<>(2);
        filter.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_SDK);
        int sdkCount = connectionManager.currentClientsCount(filter);
        if (sdkCount <= reloadCount) {
            response.setMessage("ignore");
        } else {
            reloadCount = (int) Math.max(reloadCount, sdkCount * (1 - RemoteUtils.LOADER_FACTOR));
            connectionManager.loadCount(reloadCount, request.getReloadServer());
            response.setMessage("ok");
        }
        return response;
    }
}
