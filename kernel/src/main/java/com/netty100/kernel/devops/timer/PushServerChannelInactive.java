package com.netty100.kernel.devops.timer;

import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyConnectQueue;
import com.netty100.kernel.devops.utils.WhyCloudUtils;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/5/19
 * @since 1.0.0, 2022/5/19
 */
@Slf4j
@Component
public class PushServerChannelInactive {
    
    @Autowired
    private WhyKernelProperties whyKernelProperties;

//    @Scheduled(cron = "25 * * * * ?")
    public void registerServerChannelInactiveQueue() {
        if(!whyKernelProperties.pushServerChannelInactiveSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            WhyConnectQueue.getInstance().serverChannelInactiveQueue.clear();
            return;
        }
        WhyCloudUtils.registerQueue(WhyConnectQueue.getInstance().serverChannelInactiveQueue, RequestCode.Req16.getCode(), whyKernelProperties);
    }


}
