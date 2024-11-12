package com.netty100.netty.client.idle;

import com.netty100.common.constants.CommonConstants;
//import com.why.netty.producer.WhyMessageProducerService;
import com.netty100.common.properties.WhyNettyCommonProperties;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.client.annotation.EnableWhyNettyServer;
import com.netty100.netty.client.properties.WhyNettyServerProperties;
import com.netty100.netty.client.utils.WhyServerUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 一个测试类，模拟mq消费 不断发送消息
 *
 * @author yewenhai
 * @version 1.0.0, 2022/3/31
 * @since 1.0.0, 2022/3/31
 */
@Slf4j
@Configuration
public class IdleServerProducer extends Thread implements SmartInitializingSingleton {
//    private WhyMessageProducerService whyMessageProducerService;
    private WhyNettyCommonProperties whyNettyCommonProperties;
    private WhyNettyServerProperties whyNettyServerProperties;

    @Override
    public void afterSingletonsInstantiated() {
        //启动自动消费端
        new IdleServerProducer().start();
    }

    @Override
    public void run() {
        //集成了sdk，但未使用@EnableTopeNettyServer注解来启用sdk的功能
        Map<String, Object> serviceBeanMap = WhySpringUtils.getApplicationContext().getBeansWithAnnotation(EnableWhyNettyServer.class);
        if (SysUtility.isEmpty(serviceBeanMap) || serviceBeanMap.size() <= 0) {
            return;
        }


        while(true){
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                log.error("线程休眠失败,{}", e);
            }
            List<Channel> list = new ArrayList<>();
            try {
                //没有懒加载，手动从
//                if(SysUtility.isEmpty(whyMessageProducerService)){
//                    whyMessageProducerService = WhySpringUtils.getBean(WhyMessageProducerService.class);
//                }
                if(SysUtility.isEmpty(this.whyNettyCommonProperties)){
                    this.whyNettyCommonProperties = WhySpringUtils.getBean(WhyNettyCommonProperties.class);
                }
                if(SysUtility.isEmpty(whyNettyServerProperties)){
                    whyNettyServerProperties = WhySpringUtils.getBean(WhyNettyServerProperties.class);
                }

                list = WhyServerUtils.getCacheChannelList();
                if(SysUtility.isEmpty(list)){
                    continue;
                }

                byte messageWay;
                if(whyNettyServerProperties.getTransferMode() == CommonConstants.TRANSFER_MODE_SIMPLEX){
                    messageWay = WhyMessageCode.way_simplex_idleState.getCode();
                } else {
                    messageWay = WhyMessageCode.way_s2p_idleState.getCode();
                }

                WhyNettyCommonProperties whyNettyCommonProperties = WhySpringUtils.getBean(WhyNettyCommonProperties.class);

                list.stream().forEach(channel ->{
                    if(!channel.isActive()){
                        WhyServerUtils.closeCtx(channel);
                    }else{
                        channel.writeAndFlush(WhyMessageFactory.newDefaultMessage(messageWay,
                                whyNettyCommonProperties.getMessageSource(), whyNettyCommonProperties.getMessageDest(), -1L, null, false).bytes());
                    }
                });
//                log.info("sdk-{}-心跳报文发送成功,currentChannel={}", SysUtility.getHostIp(), list.size());
            } catch (Exception e) {
                log.error("sdk-{}-心跳报文发送失败,currentChannel={}", SysUtility.getHostIp(), list.size(), e);
                continue;
            }
        }

    }
}
