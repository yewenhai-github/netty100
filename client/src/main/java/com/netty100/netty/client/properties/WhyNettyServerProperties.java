package com.netty100.netty.client.properties;

import io.netty.util.NettyRuntime;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
@ConfigurationProperties(prefix = "com.netty100.server")
@Component
public class WhyNettyServerProperties {
    private String host;
    private String port = "8981";
    private int transferMode = 1;
    private int clientType = 1;
    private int clientStartNum = 100;
    private int seletorNum = 10;
    private int executorNum = NettyRuntime.availableProcessors() * 2;
    //消息发送重试次数上限
    private int messageSendRetry = 3;
}
