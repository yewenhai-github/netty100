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

package com.netty100.cluster.common.utils;

import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;

/**
 * gRPC config for sdk.
 *
 * @author why
 */
public class TlsTypeResolve {

    /**
     * JDK SSL is very slower than OPENSSL, recommend use openSSl.
     *
     * @param provider name of ssl provider.
     * @return SslProvider.
     */
    public static SslProvider getSslProvider(String provider) {
        if (SslProvider.OPENSSL.name().equalsIgnoreCase(provider)) {
            return SslProvider.OPENSSL;
        }
        if (SslProvider.JDK.name().equalsIgnoreCase(provider)) {
            return SslProvider.JDK;
        }
        if (SslProvider.OPENSSL_REFCNT.name().equalsIgnoreCase(provider)) {
            return SslProvider.OPENSSL_REFCNT;
        }
        return SslProvider.OPENSSL;
    }
}
