package com.netty100.netty.server.connect;

import com.netty100.common.properties.WhyNettyCloudProperties;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.server.properties.WhyNettyServerProperties;
import com.netty100.netty.server.utils.WhyServerUtils;
import io.netty.bootstrap.Bootstrap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author why
 * @version 1.0.0, 2022/4/6
 * @since 1.0.0, 2022/4/6
 */
@Component
@Slf4j
@Data
public class ClientReconnect extends Thread {
    private boolean stop = false;
    private WhyNettyServerProperties whyNettyServerProperties;
    private WhyNettyCloudProperties whyNettyCloudProperties;
    private Bootstrap bootstrap;

    @Override
    public void run() {
        while (!stop){
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //服务器重连
            doCommand();
        }
    }

    /**
     * TODO cacheConnect需要根据内核节点地址去重count
     * */
    public synchronized void doCommand(){
        try {
            if(SysUtility.isEmpty(whyNettyServerProperties)){
                log.warn("client reconnect load config null");
                return;
            }

            String host = whyNettyServerProperties.getHost();
            if(SysUtility.isEmpty(whyNettyServerProperties.getHost())){
                host = WhyServerUtils.getCloudServerIps(whyNettyCloudProperties);
            }

            String port = whyNettyServerProperties.getPort();
            int clientStartNum = whyNettyServerProperties.getClientStartNum();
            String[] hosts = host.split(",");
            String[] ports = port.split(",");
            for (int i = 0; i < hosts.length; i++) {
                String tmpHost = hosts[i];
                String tmpPort = ports.length == 1 ? ports[0] : ports[i];
                if(SysUtility.isEmpty(tmpHost) || "null".equalsIgnoreCase(tmpHost)){
                    log.warn("sdk重连机制触发，服务器地址解析为空，配置文件地址={}，最终获取地址={}，", whyNettyServerProperties.getHost(), host);
                    continue;
                }

                Map<String, Integer> channelIpMap = WhyServerUtils.getChannelIpMap();
                Integer cacheConnect = SysUtility.isEmpty(channelIpMap.get(tmpHost)) ? 0 : channelIpMap.get(tmpHost);
                if(clientStartNum > cacheConnect){
                    Integer reConnect = clientStartNum - cacheConnect;
                    log.warn("sdk重连机制触发，连接至服务器{}, 当前连接数={}，正在重连连接数={}", tmpHost, cacheConnect, reConnect);
                    ClientConnectManager.clicentConnectOneServer(bootstrap, tmpHost, tmpPort, reConnect);
                }
            }
        } catch (Exception e) {
            log.error("sdk重连机制发生错误，异常={}", e);
            e.printStackTrace();
        }
    }



}
