/*
 *
 *  * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.netty100.cluster.core.cluster.remote.request;

import com.netty100.cluster.api.remote.request.Request;

/**
 * Cluster request.
 *
 * @author yewenhai
 */
public abstract class AbstractClusterRequest extends Request {
    
    private static final String CLUSTER = "cluster";
    
    @Override
    public String getModule() {
        return CLUSTER;
    }
}