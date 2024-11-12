/*
 *
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
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
 *
 */

package com.netty100.cluster.core.control;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * tps control manager.
 *
 * @author yewenhai
 * @version $Id: TpsControlManager.java, v 0.1 2021年01月09日 12:38 PM liuzunfei Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsControl {
    
    
    /**
     * alias name for control point.
     *
     * @return
     */
    String name() default "";
    
    /**
     * The point name should applied for.
     *
     * @return action type, default READ
     */
    String pointName();
    
}