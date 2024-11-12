package com.netty100.kernel.processor.service.server;

import com.netty100.kernel.annotation.EnableMessageType;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.kernel.utils.WhyChannelUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableMessageType(value = CommonConstants.type_offline_single_message)
public class OfflineSingleMessage implements MessageTypeService {
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Override
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        String key = WhyChannelUtils.getOfflineSingleKey(topeMsg);
        long expireTimestamp = System.currentTimeMillis() + topeMsg.getVariableHeader().getExpiresTime() * 1000;

//        redisTemplate.opsForValue().set(key, topeMsg);
//        redisTemplate.expireAt(key, new Date(expireTimestamp));

        return true;
    }
}
