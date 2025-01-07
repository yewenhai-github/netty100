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
@EnableMessageType(value = CommonConstants.type_offline_single_message)
public class OfflineSingleMessage implements MessageTypeService {
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Override
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        String key = WhyChannelUtils.getOfflineSingleKey(whyMsg);
        long expireTimestamp = System.currentTimeMillis() + whyMsg.getVariableHeader().getExpiresTime() * 1000;

//        redisTemplate.opsForValue().set(key, whyMsg);
//        redisTemplate.expireAt(key, new Date(expireTimestamp));

        return true;
    }
}
