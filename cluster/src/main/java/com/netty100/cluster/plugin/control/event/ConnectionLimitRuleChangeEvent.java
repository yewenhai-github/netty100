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

package com.netty100.cluster.plugin.control.event;

import com.netty100.cluster.common.notify.Event;

/**
 *  connection limit rule change event.
 *  @author zunfei.lzf
 */
public class ConnectionLimitRuleChangeEvent extends Event {
    
    private boolean external;
    
    public ConnectionLimitRuleChangeEvent(boolean external) {
        this.external = external;
    }
    
    public boolean isExternal() {
        return external;
    }
    
    public void setExternal(boolean external) {
        this.external = external;
    }
}