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

package com.netty100.cluster.naming.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Subscribers.
 *
 * @author nicholas
 * @version $Id: Subscribers.java, v 0.1 2019-05-28 下午10:47 nicholas Exp $$
 */
public class Subscribers implements Serializable {

    private static final long serialVersionUID = -3075690233070417052L;

    private List<Subscriber> subscribers;
    
    public List<Subscriber> getSubscribers() {
        return subscribers;
    }
    
    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }
}