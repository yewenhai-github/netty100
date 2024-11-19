package com.netty100.broker.devops.timer;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.entity.ClientMessageLog;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.devops.utils.WheelTimerUtils;
import com.netty100.broker.protocol.TimerRunConstants;
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
 * @author why
 * @version 1.0.0, 2022/4/25
 * @since 1.0.0, 2022/4/25
 */
@Slf4j
@Component
public class PushClientMessageLog {
    @Autowired
    private WhyKernelProperties whyKernelProperties;

    /**
     * 10秒上报一次
     */
//    @Scheduled(initialDelay = -1, fixedRate = 10000)
    public void doCommand() {
        if(!whyKernelProperties.pushClientMessageLogSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            //关闭开关时清空队列中内容，防止内存溢出
            WhyMessageQueue.getInstance().clientMessageLogQueue.clear();
            return;
        }
        addClientMessageLogTask(whyKernelProperties, false);
    }

    public static void addClientMessageLogTask(WhyKernelProperties whyKernelProperties, boolean closed){
        LinkedBlockingQueue<ClientMessageLog> clientMessageLogQueue = WhyMessageQueue.getInstance().clientMessageLogQueue;
        
        if(clientMessageLogQueue.size() > 0){
            List<ClientMessageLog> entityList = new ArrayList<ClientMessageLog>();
            clientMessageLogQueue.drainTo(entityList);

            WheelTimerUtils.hashedWheel.newTimeout(new TimerTask() {
                @Override
                public void run(final Timeout timeout) throws Exception {
                    WhyCloudUtils.postBody(whyKernelProperties, RequestCode.Req19.getCode(), entityList, entityList.size());
                }
            }, TimerRunConstants.WHEEL_DELAY, TimeUnit.MILLISECONDS);
        }
    }
}
