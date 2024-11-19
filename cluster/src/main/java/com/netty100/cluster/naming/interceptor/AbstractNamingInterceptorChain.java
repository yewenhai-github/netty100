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

package com.netty100.cluster.naming.interceptor;

import com.netty100.cluster.common.spi.CapServiceLoader;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract Naming Interceptor Chain.
 *
 * @author why
 */
public abstract class AbstractNamingInterceptorChain<T extends Interceptable>
        implements CapNamingInterceptorChain<T> {
    
    private final List<CapNamingInterceptor<T>> interceptors;
    
    protected AbstractNamingInterceptorChain(Class<? extends CapNamingInterceptor<T>> clazz) {
        this.interceptors = new LinkedList<>();
        interceptors.addAll(CapServiceLoader.load(clazz));
        interceptors.sort(Comparator.comparingInt(CapNamingInterceptor::order));
    }
    
    /**
     * Get all interceptors.
     *
     * @return interceptors list
     */
    protected List<CapNamingInterceptor<T>> getInterceptors() {
        return interceptors;
    }
    
    @Override
    public void addInterceptor(CapNamingInterceptor<T> interceptor) {
        interceptors.add(interceptor);
        interceptors.sort(Comparator.comparingInt(CapNamingInterceptor::order));
    }
    
    @Override
    public void doInterceptor(T object) {
        for (CapNamingInterceptor<T> each : interceptors) {
            if (!each.isInterceptType(object.getClass())) {
                continue;
            }
            if (each.intercept(object)) {
                object.afterIntercept();
                return;
            }
        }
        object.passIntercept();
    }
}
