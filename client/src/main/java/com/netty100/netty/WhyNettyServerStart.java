package com.netty100.netty;

import com.netty100.common.code.WhyMessageDecoder;
import com.netty100.common.code.WhyMessageEncoder;
import com.netty100.common.properties.WhyNettyCloudProperties;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.client.annotation.EnableWhyNettyServer;
import com.netty100.netty.client.channel.WhyServerConnectHandler;
import com.netty100.netty.client.channel.WhyServerIdleHandler;
import com.netty100.netty.client.channel.WhyServerMessageHandler;
import com.netty100.netty.client.connect.ClientConnectManager;
import com.netty100.netty.client.connect.ClientReconnect;
import com.netty100.netty.client.properties.WhyNettyServerProperties;
import com.netty100.netty.client.utils.WhyServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Configuration
@ConditionalOnBean(annotation = EnableWhyNettyServer.class)
@Slf4j
public class WhyNettyServerStart implements SmartInitializingSingleton {
    @Autowired
    private WhyNettyServerProperties whyNettyServerProperties;
    @Autowired
    private WhyNettyCloudProperties whyNettyCloudProperties;
    @Autowired
    private ClientReconnect clientReconnect;

    @Override
    public void afterSingletonsInstantiated() {
        new TopeNettyServer().connect(whyNettyServerProperties, whyNettyCloudProperties, clientReconnect);
    }


    private class TopeNettyServer {

        public void connect(WhyNettyServerProperties whyNettyServerProperties, WhyNettyCloudProperties whyNettyCloudProperties, ClientReconnect clientReconnect) {
            EventExecutorGroup group = new DefaultEventExecutorGroup(whyNettyServerProperties.getExecutorNum());
            WhyServerConnectHandler whyServerConnectHandler = new WhyServerConnectHandler();
            WhyServerIdleHandler whyServerIdleHandler = new WhyServerIdleHandler();
            WhyServerMessageHandler whyServerMessageHandler = new WhyServerMessageHandler();

            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup(whyNettyServerProperties.getSeletorNum()));
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) {
                    //字符串编码器，一定要加在SimpleClientHandler 的上面
                    ch.pipeline().addLast("encoder", new WhyMessageEncoder());
                    ch.pipeline().addLast("decoder", new WhyMessageDecoder());
                    ch.pipeline().addLast("heartBeatHandler", new IdleStateHandler(0, 90, 0, TimeUnit.SECONDS));
                    //找到他的管道 增加他的handler
                    ch.pipeline().addLast(group, whyServerConnectHandler, whyServerIdleHandler, whyServerMessageHandler);
                }
            });
            //启动Netty客户端
            String host = whyNettyServerProperties.getHost();
            if(SysUtility.isEmpty(whyNettyServerProperties.getHost())){
                host = WhyServerUtils.getCloudServerIps(whyNettyCloudProperties);
            }
            ClientConnectManager.clicentConnectManyServer(bootstrap, host, whyNettyServerProperties.getPort(), whyNettyServerProperties.getClientStartNum());
            //启动实现服务器重连机制：netty平台重启，服务器会自动断开对应的连接，服务器需要自动重连
            clientReconnect.setWhyNettyServerProperties(whyNettyServerProperties);
            clientReconnect.setWhyNettyCloudProperties(whyNettyCloudProperties);
            clientReconnect.setBootstrap(bootstrap);
            clientReconnect.start();
        }
    }
}
