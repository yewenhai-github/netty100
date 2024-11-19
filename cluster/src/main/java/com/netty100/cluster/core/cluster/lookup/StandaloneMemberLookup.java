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

package com.netty100.cluster.core.cluster.lookup;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.core.cluster.AbstractMemberLookup;
import com.netty100.cluster.core.cluster.MemberUtil;
import com.netty100.cluster.sys.env.EnvUtil;

import java.util.Collections;

/**
 * Member node addressing mode in stand-alone mode.
 *
 * @author why
 */
public class StandaloneMemberLookup extends AbstractMemberLookup {
    
    @Override
    public void doStart() {
        String url = EnvUtil.getLocalAddress();
        afterLookup(MemberUtil.readServerConf(Collections.singletonList(url)));
    }
    
    @Override
    protected void doDestroy() throws CapException {
    
    }
    
    @Override
    public boolean useKernelServer() {
        return false;
    }
}
