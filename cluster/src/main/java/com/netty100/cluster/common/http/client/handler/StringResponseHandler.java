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

package com.netty100.cluster.common.http.client.handler;

import com.netty100.cluster.common.http.client.response.HttpClientResponse;
import com.netty100.cluster.common.utils.IoUtils;
import com.netty100.cluster.common.http.HttpRestResult;
import com.netty100.cluster.common.http.param.Header;

import java.lang.reflect.Type;

/**
 * string response handler, Mainly converter response type as string type.
 *
 * @author yewenhai
 */
public class StringResponseHandler extends AbstractResponseHandler<String> {
    
    @Override
    public HttpRestResult<String> convertResult(HttpClientResponse response, Type responseType) throws Exception {
        final Header headers = response.getHeaders();
        String extractBody = IoUtils.toString(response.getBody(), headers.getCharset());
        return new HttpRestResult<>(headers, response.getStatusCode(), extractBody, null);
    }
}
