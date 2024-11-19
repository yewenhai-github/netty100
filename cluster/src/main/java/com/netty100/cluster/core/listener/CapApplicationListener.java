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

package com.netty100.cluster.core.listener;

import com.netty100.cluster.core.code.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Cap Application Listener, execute init process.
 *
 * @author why
 * @since 1.4.1
 */
public interface CapApplicationListener {
    
    /**
     * {@link SpringApplicationRunListener#starting}.
     */
    default void starting() {
    }
    
    /**
     * {@link SpringApplicationRunListener#environmentPrepared}.
     *
     * @param environment environment
     */
    default void environmentPrepared(ConfigurableEnvironment environment) {
    }
    
    /**
     * {@link SpringApplicationRunListener#contextLoaded}.
     *
     * @param context context
     */
    default void contextPrepared(ConfigurableApplicationContext context) {
    }
    
    /**
     * {@link SpringApplicationRunListener#contextLoaded}.
     *
     * @param context context
     */
    default void contextLoaded(ConfigurableApplicationContext context) {
    }
    
    /**
     * {@link SpringApplicationRunListener#started}.
     *
     * @param context context
     */
    default void started(ConfigurableApplicationContext context) {
    }
    
    /**
     * {@link SpringApplicationRunListener#running}.
     *
     * @param context context
     */
    default void running(ConfigurableApplicationContext context) {
    }
    
    /**
     * {@link SpringApplicationRunListener#failed}.
     *
     * @param context   context
     * @param exception exception
     */
    default void failed(ConfigurableApplicationContext context, Throwable exception) {
    }
}
