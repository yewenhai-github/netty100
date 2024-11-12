/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
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

package com.netty100.cluster.api.exception.api;

import com.netty100.cluster.api.common.Constants;
import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.model.v2.ErrorCode;
import com.netty100.cluster.api.utils.StringUtils;

/** Exception for open API. <BR/>
 * errCode          ->  HTTP status code        inherited from {@link CapException} <BR/>
 * errMsg           ->  detail error message    inherited from {@link CapException} <BR/>
 * detailErrCode    ->  error code for api v2.0 <BR/>
 * errAbstract      ->  abstract error message for api v2.0
 * @author yewenhai
 * @date 2022/7/22
 */
public class CapApiException extends CapException {
    
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 2245627968556056573L;
    
    /**
     * error code for api v2.0.
     */
    private int detailErrCode;
    
    /**
     * abstract error description for api v2.0.
     */
    private String errAbstract;
    
    public CapApiException() {
    }
    
    public CapApiException(int statusCode, ErrorCode errorCode, Throwable throwable, String message) {
        super(statusCode, message, throwable);
        this.detailErrCode = errorCode.getCode();
        this.errAbstract = errorCode.getMsg();
    }
    
    public CapApiException(int statusCode, ErrorCode errorCode, String message) {
        super(statusCode, message);
        this.detailErrCode = errorCode.getCode();
        this.errAbstract = errorCode.getMsg();
    }
    
    public int getDetailErrCode() {
        return detailErrCode;
    }
    
    public String getErrAbstract() {
        if (!StringUtils.isBlank(this.errAbstract)) {
            return this.errAbstract;
        }
        return Constants.NULL;
    }
    
    public void setErrAbstract(String errAbstract) {
        this.errAbstract = errAbstract;
    }
}
