package com.netty100.broker.processor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestPair<T,K> {
    private T requestProcessor;
    private K executorService;
}
