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

package com.netty100.cluster.api.remote.response;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.exception.runtime.CapRuntimeException;

/**
 * UnKnowResponse.
 *
 * @author why
 * @version $Id: UnKnowResponse.java, v 0.1 2020年07月16日 9:47 PM liuzunfei Exp $
 */
public class ErrorResponse extends Response {
    
    /**
     * build an error response.
     *
     * @param errorCode errorCode
     * @param msg msg
     * @return response
     */
    public static Response build(int errorCode, String msg) {
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(errorCode, msg);
        return response;
    }
    
    /**
     * build an error response.
     *
     * @param exception exception
     * @return response
     */
    public static Response build(Throwable exception) {
        int errorCode;
        if (exception instanceof CapException) {
            errorCode = ((CapException) exception).getErrCode();
        } else if (exception instanceof CapRuntimeException) {
            errorCode = ((CapRuntimeException) exception).getErrCode();
        } else {
            errorCode = ResponseCode.FAIL.getCode();
        }
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(errorCode, exception.getMessage());
        return response;
    }
    
}
