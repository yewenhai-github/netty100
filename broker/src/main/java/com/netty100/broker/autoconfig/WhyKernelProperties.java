package com.netty100.broker.autoconfig;

import com.netty100.cluster.core.cluster.lookup.AddressServerMemberLookup;
import com.netty100.cluster.sys.env.EnvUtil;
import io.netty.util.NettyRuntime;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
@ConfigurationProperties(prefix = "com.netty100.kernel")
public class WhyKernelProperties {
    //内核端口号
    private Integer port = 8981;
    //work中每个seletor开启的业务工作线程组
    private Integer workGroup = NettyRuntime.availableProcessors() * 2;
    //TCP连接同时存在队列长度
    private Integer backLog = 8 * 1024;
    //idle监听读事件探活对应事件间隔（s）
    private long readerIdleTime = 300;

    private boolean userRedisCache = true;

    private int serverCacheChannelReTimes = 3;

    private int highWater = 256 * 1024;
    private int lowWater = 16 * 1024;

    private boolean enableTlsSever = false;
    private boolean isNeedClientAuth = false;

    public boolean pullKernelConfigServerListSwitch = true;
    public boolean pullKernelNameServerListSwitch = true;
    public boolean pushKernelNameServerStartSwitch = true;
    public boolean pushKernelNameServerRegisterSwitch = true;
    public boolean pushKernelNameServerExitSwitch = true;
    public boolean pushKernelMessageLogSwitch = true;

    public boolean pushClientChannelActiveSwitch = true;
    public boolean pushClientChannelInactiveSwitch = true;
    public boolean pushClientChannelExceptionCaughtSwitch = true;
    public boolean pushClientChannelIdelActiveSwitch = true;
    public boolean pushClientChannelIdleInactiveSwitch = true;
    public boolean pushClientChannelLogSwitch = true;
    public boolean pushClientMessageLogSwitch = true;

    public boolean pushServerChannelActiveSwitch = true;
    public boolean pushServerChannelInactiveSwitch = true;
    public boolean pushServerChannelExceptionCaughtSwitch = true;
    public boolean pushServerChannelIdelActiveSwitch = true;
    public boolean pushstatisticRegisterSwitch = true;

    //TODO 客户端退出后，推送给服务器的报文默认降级为普通报文
    public boolean variableFlag = false;

    //域名服务器地址
    private String domain;//域名地址
    //不失效的后端token
    private String token;
    private int timeOut = 3;

    public Integer getPort() {
        return EnvUtil.getProperty(EnvUtil.SERVER_PORT_PROPERTY, Integer.class, 8981);
    }

    public String getDomain() {
        return EnvUtil.getProperty(AddressServerMemberLookup.NAME_SERVER_DOMAIN_PROPERTY, String.class, AddressServerMemberLookup.DEFAULT_SERVER_DOMAIN);
    }
    public String getToken() {
        return EnvUtil.getProperty(AddressServerMemberLookup.NAME_SERVER_TOKEN_PROPERTY, String.class, "");
    }

    public boolean isVariableFlag() {
        return variableFlag;
    }
}
