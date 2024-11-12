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

import com.netty100.cluster.api.remote.request.RequestMeta;
import com.netty100.cluster.api.remote.request.HealthCheckRequest;
import com.netty100.cluster.api.remote.response.HealthCheckResponse;
import com.netty100.cluster.core.control.TpsControl;
import org.springframework.stereotype.Component;

/**
 * push response  to clients.
 *
 * @author yewenhai
 * @version $Id: PushService.java, v 0.1 2021年07月17日 1:12 PM liuzunfei Exp $
 */
@Component
public class HealthCheckRequestHandler extends RequestHandler<HealthCheckRequest, HealthCheckResponse> {
    
    @Override
    @TpsControl(pointName = "HealthCheck")
    public HealthCheckResponse handle(HealthCheckRequest request, RequestMeta meta) {
        return new HealthCheckResponse();
    }
}
