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
public class PushClientChannelExceptionCaught {
    
    @Autowired
    private WhyKernelProperties whyKernelProperties;

//    @Scheduled(cron = "10 * * * * ?")
    public void registerClientChannelExceptionCaughtQueue() {
        if(!whyKernelProperties.pushClientChannelExceptionCaughtSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            WhyConnectQueue.getInstance().clientChannelExceptionCaughtQueue.clear();
            return;
        }
        WhyCloudUtils.registerQueue(WhyConnectQueue.getInstance().clientChannelExceptionCaughtQueue, RequestCode.Req13.getCode(), whyKernelProperties);
    }


}
