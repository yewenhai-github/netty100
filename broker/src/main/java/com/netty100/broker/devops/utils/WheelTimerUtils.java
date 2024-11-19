package com.netty100.broker.devops.utils;

import io.netty.util.HashedWheelTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author why
 * @version 1.0.0, 2022/5/27
 * @since 1.0.0, 2022/5/27
 */
public class WheelTimerUtils {
    public final static HashedWheelTimer hashedWheel = new HashedWheelTimer(Executors.defaultThreadFactory(), 100,
            TimeUnit.MILLISECONDS, 512, true, 10000);

}
