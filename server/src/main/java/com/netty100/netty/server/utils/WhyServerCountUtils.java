package com.netty100.netty.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Component
@Slf4j
public class WhyServerCountUtils {
    //服务端统计
    public static AtomicInteger countTotalRead = new AtomicInteger(0);
    public static AtomicInteger countSuccessRead = new AtomicInteger(0);
    public static AtomicInteger countTotalWrite = new AtomicInteger(0);
    public static AtomicInteger countSuccessWrite = new AtomicInteger(0);
    public static AtomicInteger connectTotal = new AtomicInteger(0);


}
