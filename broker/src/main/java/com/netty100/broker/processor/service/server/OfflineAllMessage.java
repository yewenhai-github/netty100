package com.netty100.broker.processor.service.server;

import com.netty100.broker.annotation.EnableMessageType;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableMessageType(value = CommonConstants.type_offline_all_message)
public class OfflineAllMessage implements MessageTypeService {
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Override
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        String key = WhyChannelUtils.getOfflineAllKey(topeMsg);
        long expireTimestamp = System.currentTimeMillis() + topeMsg.getVariableHeader().getExpiresTime() * 1000;

//        redisTemplate.opsForValue().set(key, topeMsg);
//        redisTemplate.expireAt(key, new Date(expireTimestamp));

        return true;
    }
}
