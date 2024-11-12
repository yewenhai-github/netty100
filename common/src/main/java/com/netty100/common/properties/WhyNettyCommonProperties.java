package com.netty100.common.properties;

import com.netty100.common.protocol.WhyMessageQoS;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
@ConfigurationProperties(prefix = "com.netty100.common")
@Component
public class WhyNettyCommonProperties {
    private byte messageSource = -1;
    private byte messageDest = -1;
    private byte messageSerialize = 2;
    private byte messageWay = -1;

    private byte apiVersion = 0;
    private boolean twoWayMsg = true;
    private boolean firstTime = true;
    private int qosLevel = WhyMessageQoS.AT_MOST_ONCE.value();
    private boolean rewrite = true;

    //netty远程调用默认参数
    private int clientWorkerThreads = 4;
    private int connectTimeoutMillis = 3000;
    private int clientSocketSndBufSize = 65535;
    private int clientSocketRcvBufSize = 65535;
    private int clientChannelMaxIdleTimeSeconds = 120;
    private int clientOnewaySemaphoreValue = 65535;

}
