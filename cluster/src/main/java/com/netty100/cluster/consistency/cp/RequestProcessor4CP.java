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

package com.netty100.cluster.consistency.cp;

import com.netty100.cluster.consistency.snapshot.SnapshotOperation;
import com.netty100.cluster.consistency.RequestProcessor;

import java.util.Collections;
import java.util.List;

/**
 * log processor for cp.
 *
 * @author why
 */
@SuppressWarnings("all")
public abstract class RequestProcessor4CP extends RequestProcessor {
    
    
    /**
     * Discovery snapshot handler It is up to LogProcessor to decide which SnapshotOperate should be loaded and saved by
     * itself.
     *
     * @return {@link List <SnapshotOperate>}
     */
    public List<SnapshotOperation> loadSnapshotOperate() {
        return Collections.emptyList();
    }
    
}
