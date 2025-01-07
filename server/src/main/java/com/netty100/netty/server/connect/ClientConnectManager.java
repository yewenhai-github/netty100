package com.netty100.netty.server.connect;

import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.server.annotation.EnableWhyNettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author why
 * @version 1.0.0, 2022/4/6
 * @since 1.0.0, 2022/4/6
 */
@Slf4j
public class ClientConnectManager {

    public static void clicentConnectManyServer(Bootstrap bootstrap, final String serverHost, final String beginPort, int connectionCount){
        //集成了sdk，但未使用@EnableWhyNettyServer注解来启用sdk的功能
        Map<String, Object> serviceBeanMap = WhySpringUtils.getApplicationContext().getBeansWithAnnotation(EnableWhyNettyServer.class);
        if (SysUtility.isEmpty(serviceBeanMap) || serviceBeanMap.size() <= 0) {
            return;
        }

        String[] ips = serverHost.split(",");
        String[] ports = beginPort.split(",");

        for (int i = 0; i < ips.length; i++) {
            String ip = ips[i];
            String port = ports.length == 1 ? ports[0] : ports[i];

            clicentConnectOneServer(bootstrap, ip, port, connectionCount);
        }
    }

    public static void clicentConnectOneServer(Bootstrap bootstrap, final String ip, final String port, int connectionCount){
        //集成了sdk，但未使用@EnableWhyNettyServer注解来启用sdk的功能
        Map<String, Object> serviceBeanMap = WhySpringUtils.getApplicationContext().getBeansWithAnnotation(EnableWhyNettyServer.class);
        if (SysUtility.isEmpty(serviceBeanMap) || serviceBeanMap.size() <= 0) {
            return;
        }

        for (int i = 1; i <= connectionCount; ++i) {
            try {
                ChannelFuture channelFuture = bootstrap.connect(ip, Integer.parseInt(port));
                int finalI = i;
                channelFuture.addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.info("连接失败({}), "+ip+":"+port+" 退出!", finalI);
                    }
                });
                channelFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
