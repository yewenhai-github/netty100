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

package com.netty100.cluster.naming.misc;

import com.netty100.cluster.sys.env.EnvUtil;
import com.netty100.cluster.naming.constants.Constants;
import org.springframework.stereotype.Component;

/**
 * Stores some configurations for Distro protocol.
 *
 * @author why
 * @since 1.0.0
 */
@Component
public class GlobalConfig {
    
    public boolean isDataWarmup() {
        return EnvUtil.getProperty(Constants.DATA_WARMUP, Boolean.class, false);
    }
    
    public boolean isExpireInstance() {
        return EnvUtil.getProperty(Constants.EXPIRE_INSTANCE, Boolean.class, true);
    }
    
    public static Long getEmptyServiceCleanInterval() {
        return EnvUtil.getProperty(Constants.EMPTY_SERVICE_CLEAN_INTERVAL, Long.class, 60000L);
    }
    
    public static Long getEmptyServiceExpiredTime() {
        return EnvUtil.getProperty(Constants.EMPTY_SERVICE_EXPIRED_TIME, Long.class, 60000L);
    }
    
    public static Long getExpiredMetadataCleanInterval() {
        return EnvUtil.getProperty(Constants.EXPIRED_METADATA_CLEAN_INTERVAL, Long.class, 5000L);
    }
    
    public static Long getExpiredMetadataExpiredTime() {
        return EnvUtil.getProperty(Constants.EXPIRED_METADATA_EXPIRED_TIME, Long.class, 60000L);
    }
    
}
