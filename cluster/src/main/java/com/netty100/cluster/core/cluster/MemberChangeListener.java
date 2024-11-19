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

package com.netty100.cluster.core.cluster;

import com.netty100.cluster.common.notify.Event;
import com.netty100.cluster.common.notify.listener.Subscriber;

/**
 * Node change listeners.
 *
 * @author why
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class MemberChangeListener extends Subscriber<MembersChangeEvent> {
    
    /**
     * return NodeChangeEvent.class info.
     *
     * @return {@link MembersChangeEvent#getClass()}
     */
    @Override
    public Class<? extends Event> subscribeType() {
        return MembersChangeEvent.class;
    }
    
    /**
     * Whether to ignore expired events.
     *
     * @return default value is {@link Boolean#TRUE}
     */
    @Override
    public boolean ignoreExpireEvent() {
        return true;
    }
}
