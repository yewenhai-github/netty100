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

package com.netty100.cluster.core.remote;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.remote.request.Request;
import com.netty100.cluster.api.remote.request.RequestMeta;
import com.netty100.cluster.api.remote.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * interceptor fo request.
 *
 * @author why
 * @version $Id: AbstractRequestFilter.java, v 0.1 2020年09月14日 11:46 AM liuzunfei Exp $
 */
public abstract class AbstractRequestFilter {
    
    @Autowired
    private RequestFilters requestFilters;
    
    public AbstractRequestFilter() {
    }
    
    @PostConstruct
    public void init() {
        requestFilters.registerFilter(this);
    }
    
    protected Class getResponseClazz(Class handlerClazz) throws CapException {
        ParameterizedType parameterizedType = (ParameterizedType) handlerClazz.getGenericSuperclass();
        try {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return Class.forName(actualTypeArguments[1].getTypeName());
            
        } catch (Exception e) {
            throw new CapException(CapException.SERVER_ERROR, e);
        }
    }
    
    protected Method getHandleMethod(Class handlerClazz) throws CapException {
        try {
            Method method = handlerClazz.getMethod("handle", Request.class, RequestMeta.class);
            return method;
        } catch (NoSuchMethodException e) {
            throw new CapException(CapException.SERVER_ERROR, e);
        }
    }
    
    protected <T> Response getDefaultResponseInstance(Class handlerClazz) throws CapException {
        ParameterizedType parameterizedType = (ParameterizedType) handlerClazz.getGenericSuperclass();
        try {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Response) Class.forName(actualTypeArguments[1].getTypeName()).newInstance();
            
        } catch (Exception e) {
            throw new CapException(CapException.SERVER_ERROR, e);
        }
    }
    
    /**
     * filter request.
     *
     * @param request      request.
     * @param meta         request meta.
     * @param handlerClazz request handler clazz.
     * @return response
     * @throws CapException NacosException.
     */
    protected abstract Response filter(Request request, RequestMeta meta, Class handlerClazz) throws CapException;
}
