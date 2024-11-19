package com.netty100.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.LongAdder;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/15
 */
@Data
public class ServerReconnectCalcDto {
    public static LongAdder reconnectErrorCount = new LongAdder();
    public static LongAdder reconnectIdleCount = new LongAdder();
}
