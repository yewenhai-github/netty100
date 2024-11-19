package com.netty100.broker.devops.timer;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyConnectQueue;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/5/19
 * @since 1.0.0, 2022/5/19
 */
@Slf4j
@Component
public class PushServerChannelActive {
    @Autowired
    private WhyKernelProperties whyKernelProperties;

//    @Scheduled(fixedRate = 5000)
    public void registerServerChannelActiveQueue() {
        if(!whyKernelProperties.pushServerChannelActiveSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            WhyConnectQueue.getInstance().serverChannelActiveQueue.clear();
            log.warn("registerServerChannelActiveQueue上报功能已禁用，!pushServerChannelActiveSwitch={},domain={}", !whyKernelProperties.pushServerChannelActiveSwitch, whyKernelProperties.getDomain());
            return;
        }
        WhyCloudUtils.registerQueue(WhyConnectQueue.getInstance().serverChannelActiveQueue, RequestCode.Req15.getCode(), whyKernelProperties);
    }


}
