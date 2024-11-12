package com.netty100.kernel.devops.timer;

import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyConnectQueue;
import com.netty100.kernel.devops.utils.WhyCloudUtils;
import com.netty100.kernel.devops.utils.WheelTimerUtils;
import com.netty100.kernel.protocol.TimerRunConstants;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/25
 * @since 1.0.0, 2022/4/25
 */
@Slf4j
@Component
public class PushServerChannelIdelActive {
    @Autowired
    private WhyKernelProperties whyKernelProperties;

    /**
     * 10秒上报一次
     */
//    @Scheduled(initialDelay = -1, fixedRate = 10000)
    public void doCommand() {
        if(!whyKernelProperties.pushServerChannelIdelActiveSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            WhyConnectQueue.getInstance().serverChannelIdleActiveQueue.clear();
            return;
        }
        addServerChannelIdleActiveTask(whyKernelProperties, false);
    }

    public static void addServerChannelIdleActiveTask(WhyKernelProperties whyKernelProperties, boolean closed){
        LinkedBlockingQueue<Object> idleQueue = WhyConnectQueue.getInstance().serverChannelIdleActiveQueue;

        if(idleQueue.size() > 0){
            List<Object> entityList = new ArrayList<Object>();
            idleQueue.drainTo(entityList);

            WheelTimerUtils.hashedWheel.newTimeout(new TimerTask() {
                @Override
                public void run(final Timeout timeout) throws Exception {
                    WhyCloudUtils.postBody(whyKernelProperties, RequestCode.Req22.getCode(), entityList, entityList.size());
                }
            }, TimerRunConstants.WHEEL_DELAY, TimeUnit.MILLISECONDS);
        }
    }
}
