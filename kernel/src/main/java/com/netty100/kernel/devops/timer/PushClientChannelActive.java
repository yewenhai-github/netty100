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
public class PushClientChannelActive {   
    @Autowired
    private WhyKernelProperties whyKernelProperties;

//    @Scheduled(fixedRate = 5000)
    public void registerClientChannelActiveQueue() {
        if(!whyKernelProperties.pushClientChannelActiveSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            WhyConnectQueue.getInstance().clientChannelActiveQueue.clear();
            log.warn("registerClientChannelActiveQueue上报功能已禁用，!pushClientChannelActiveSwitch={},domain={}", !whyKernelProperties.pushClientChannelActiveSwitch, whyKernelProperties.getDomain());
            return;
        }

        WhyCloudUtils.registerQueue(WhyConnectQueue.getInstance().clientChannelActiveQueue, RequestCode.Req11.getCode(), whyKernelProperties);
    }


}
