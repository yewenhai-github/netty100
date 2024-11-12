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
import com.netty100.cluster.api.remote.request.ServerLoaderInfoRequest;
import com.netty100.cluster.api.remote.response.ServerLoaderInfoResponse;
import com.netty100.cluster.core.remote.ConnectionManager;
import com.netty100.cluster.core.remote.RequestHandler;
import com.netty100.cluster.sys.env.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * request handler to handle server loader info.
 *
 * @author yewenhai
 * @version $Id: ServerLoaderInfoRequestHandler.java, v 0.1 2020年09月03日 2:51 PM liuzunfei Exp $
 */
@Component
public class ServerLoaderInfoRequestHandler extends RequestHandler<ServerLoaderInfoRequest, ServerLoaderInfoResponse> {
    
    @Autowired
    private ConnectionManager connectionManager;
    
    @Override
    public ServerLoaderInfoResponse handle(ServerLoaderInfoRequest request, RequestMeta meta) throws CapException {
        ServerLoaderInfoResponse serverLoaderInfoResponse = new ServerLoaderInfoResponse();
        serverLoaderInfoResponse.putMetricsValue("conCount", String.valueOf(connectionManager.currentClientsCount()));
        Map<String, String> filter = new HashMap<>(2);
        filter.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_SDK);
        serverLoaderInfoResponse.putMetricsValue("sdkConCount", String.valueOf(connectionManager.currentClientsCount(filter)));
        serverLoaderInfoResponse.putMetricsValue("load", String.valueOf(EnvUtil.getLoad()));
        serverLoaderInfoResponse.putMetricsValue("cpu", String.valueOf(EnvUtil.getCpu()));
        
        return serverLoaderInfoResponse;
    }
    
}
