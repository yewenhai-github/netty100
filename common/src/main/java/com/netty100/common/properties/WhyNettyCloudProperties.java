package com.netty100.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@ConfigurationProperties(prefix = "com.netty100.cloud")
@Component
@Data
public class WhyNettyCloudProperties {
    //域名服务器地址
    private String domain;//域名地址
    //不失效的后端token
    private String token;
    //请求超时时间，单位秒
    private int timeOut = 3;
    //用于三个api接口对外的入参，填写对应集群的英文简称，比如：english、math、chinese
    private String cluster;
}
