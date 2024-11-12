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

package com.netty100.cluster.plugin.control.connection.cap;

import com.netty100.cluster.common.utils.JacksonUtils;
import com.netty100.cluster.plugin.control.Loggers;
import com.netty100.cluster.plugin.control.connection.ConnectionControlManager;
import com.netty100.cluster.plugin.control.connection.request.ConnectionCheckRequest;
import com.netty100.cluster.plugin.control.connection.response.ConnectionCheckCode;
import com.netty100.cluster.plugin.control.connection.response.ConnectionCheckResponse;
import com.netty100.cluster.plugin.control.connection.rule.ConnectionControlRule;

/**
 * connection control manager.
 *
 * @author yewenhai
 */
public class CapConnectionControlManager extends ConnectionControlManager {
    
    @Override
    public String getName() {
        return "cap";
    }
    
    public CapConnectionControlManager() {
        super();
    }
    
    @Override
    public void applyConnectionLimitRule(ConnectionControlRule connectionControlRule) {
        super.connectionControlRule = connectionControlRule;
        Loggers.CONTROL.info("Connection control rule updated to ->" + (this.connectionControlRule == null ? null
                : JacksonUtils.toJson(this.connectionControlRule)));
        
    }
    
    @Override
    public ConnectionCheckResponse check(ConnectionCheckRequest connectionCheckRequest) {
        ConnectionCheckResponse connectionCheckResponse = new ConnectionCheckResponse();
        connectionCheckResponse.setSuccess(true);
        connectionCheckResponse.setCode(ConnectionCheckCode.CHECK_SKIP);
        return connectionCheckResponse;
    }
    
}
