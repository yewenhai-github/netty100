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
import com.netty100.cluster.api.remote.request.Request;
import com.netty100.cluster.api.remote.request.RequestMeta;
import com.netty100.cluster.api.remote.response.Response;
import com.netty100.cluster.core.utils.Loggers;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Cap based request handler.
 *
 * @author why
 * @author why
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class RequestHandler<T extends Request, S extends Response> {
    
    @Autowired
    private RequestFilters requestFilters;
    
    /**
     * Handler request.
     *
     * @param request request
     * @param meta    request meta data
     * @return response
     * @throws CapException cap exception when handle request has problem.
     */
    public Response handleRequest(T request, RequestMeta meta) throws CapException {
        for (AbstractRequestFilter filter : requestFilters.filters) {
            try {
                Response filterResult = filter.filter(request, meta, this.getClass());
                if (filterResult != null && !filterResult.isSuccess()) {
                    return filterResult;
                }
            } catch (Throwable throwable) {
                Loggers.REMOTE.error("filter error", throwable);
            }
            
        }
        return handle(request, meta);
    }
    
    /**
     * Handler request.
     *
     * @param request request
     * @param meta    request meta data
     * @return response
     * @throws CapException cap exception when handle request has problem.
     */
    public abstract S handle(T request, RequestMeta meta) throws CapException;
    
}
