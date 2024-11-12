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

package com.netty100.cluster.core.distributed.distro.exception;

/**
 * Distro exception.
 *
 * @author yewenhai
 */
public class DistroException extends RuntimeException {
    
    private static final long serialVersionUID = 1711141952413139786L;
    
    public DistroException(String message) {
        super(message);
    }
    
    public DistroException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getMessage() {
        return "[DISTRO-EXCEPTION]" + super.getMessage();
    }
}
