/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package com.netty100.cluster.naming.remote.rpc.handler;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.remote.request.RequestMeta;
import com.netty100.cluster.api.remote.response.ResponseCode;
import com.netty100.cluster.core.distributed.distro.DistroProtocol;
import com.netty100.cluster.core.distributed.distro.entity.DistroData;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.core.remote.RequestHandler;
import com.netty100.cluster.naming.cluster.remote.request.DistroDataRequest;
import com.netty100.cluster.naming.cluster.remote.response.DistroDataResponse;
import com.netty100.cluster.naming.consistency.ephemeral.distro.v2.DistroClientDataProcessor;
import com.netty100.cluster.naming.misc.Loggers;
import org.springframework.stereotype.Component;

/**
 * Distro data request handler.
 *
 * @author why
 */
@Component
public class DistroDataRequestHandler extends RequestHandler<DistroDataRequest, DistroDataResponse> {
    
    private final DistroProtocol distroProtocol;
    
    public DistroDataRequestHandler(DistroProtocol distroProtocol) {
        this.distroProtocol = distroProtocol;
    }

    //DistroDataRequestHandler用于处理Distro协议相关的RPC请求，可以看出ADD、CHANGE、DELETE都是通过handleSyncData方法进行处理。
    @Override
    public DistroDataResponse handle(DistroDataRequest request, RequestMeta meta) throws CapException {
        try {
            switch (request.getDataOperation()) {
                case VERIFY:
                    return handleVerify(request.getDistroData(), meta);
                case SNAPSHOT:
                    return handleSnapshot();
                case ADD:
                case CHANGE:
                case DELETE:
                    return handleSyncData(request.getDistroData());
                case QUERY:
                    return handleQueryData(request.getDistroData());
                default:
                    return new DistroDataResponse();
            }
        } catch (Exception e) {
            Loggers.DISTRO.error("[DISTRO-FAILED] distro handle with exception", e);
            DistroDataResponse result = new DistroDataResponse();
            result.setErrorCode(ResponseCode.FAIL.getCode());
            result.setMessage("handle distro request with exception");
            return result;
        }
    }
    
    private DistroDataResponse handleVerify(DistroData distroData, RequestMeta meta) {
        DistroDataResponse result = new DistroDataResponse();
        if (!distroProtocol.onVerify(distroData, meta.getClientIp())) {
            result.setErrorInfo(ResponseCode.FAIL.getCode(), "[DISTRO-FAILED] distro data verify failed");
        }
        return result;
    }
    
    private DistroDataResponse handleSnapshot() {
        DistroDataResponse result = new DistroDataResponse();
        DistroData distroData = distroProtocol.onSnapshot(DistroClientDataProcessor.TYPE);
        result.setDistroData(distroData);
        return result;
    }
    
    private DistroDataResponse handleSyncData(DistroData distroData) {
        DistroDataResponse result = new DistroDataResponse();
        if (!distroProtocol.onReceive(distroData)) {
            result.setErrorCode(ResponseCode.FAIL.getCode());
            result.setMessage("[DISTRO-FAILED] distro data handle failed");
        }
        return result;
    }
    
    private DistroDataResponse handleQueryData(DistroData distroData) {
        DistroDataResponse result = new DistroDataResponse();
        DistroKey distroKey = distroData.getDistroKey();
        DistroData queryData = distroProtocol.onQuery(distroKey);
        result.setDistroData(queryData);
        return result;
    }
}
