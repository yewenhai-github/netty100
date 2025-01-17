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

package com.netty100.cluster.plugin.control.tps.cap;

import com.netty100.cluster.plugin.control.tps.TpsBarrier;
import com.netty100.cluster.plugin.control.tps.TpsBarrierCreator;

/**
 * default cap tps barrier creator.
 *
 * @author why
 */
public class DefaultCapTpsBarrierCreator implements TpsBarrierCreator {
    
    @Override
    public String getName() {
        return "cap";
    }
    
    @Override
    public TpsBarrier createTpsBarrier(String pointName) {
        return new CapTpsBarrier(pointName);
    }
}
