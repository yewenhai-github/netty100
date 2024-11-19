package com.netty100.broker.utils;

import com.netty100.broker.protocol.TimerRunConstants;
import io.netty.channel.Channel;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author why
 * @version 1.0.0, 2022/7/21
 * @since 1.0.0, 2022/7/21
 */
@Slf4j
public class WhyChannelPreClose {
    public final static HashedWheelTimer hashedWheel = new HashedWheelTimer(Executors.defaultThreadFactory(), 100,
            TimeUnit.MILLISECONDS, 512, true, 100000);

    public static void proClose(Channel channel) {
        hashedWheel.newTimeout(timeout -> {
            channel.close();
//            log.info("用户强制下线成功，ip={},channelId={}", SysUtility.getHostIp(), channel.id());
        }, TimerRunConstants.WHEEL_DELAY, TimeUnit.MILLISECONDS);
    }
}
