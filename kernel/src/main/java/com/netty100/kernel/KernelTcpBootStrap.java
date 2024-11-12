package com.netty100.kernel;

import com.netty100.kernel.autoconfig.KernelBossNioEventLoopGroup;
import com.netty100.kernel.autoconfig.KernelWorkerNioEventLoopGroup;
import com.netty100.kernel.autoconfig.WhyKernelContainer;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.channel.WhyConnectHandler;
import com.netty100.kernel.channel.WhyIdleStateHandler;
import com.netty100.kernel.channel.WhyMessageHandler;
import com.netty100.kernel.protocol.ssl.SslFactory;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.code.WhyMessageDecoder;
import com.netty100.common.code.WhyMessageEncoder;
import com.netty100.common.properties.WhyNettyCommonProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
@Slf4j
@Component
public class KernelTcpBootStrap implements SmartInitializingSingleton {
    
    @Autowired
    private WhyKernelProperties whyKernelProperties;
    @Autowired
    private WhyNettyCommonProperties whyNettyCommonProperties;
    @Resource
    private SslFactory sslFactory;

    @Override
    public void afterSingletonsInstantiated() {
//        TopeNettyKernelProperties topeNettyKernelProperties = getTopeNettyKernelProperties();
//        TopeKernelProperties topeKernelProperties = getTopeNettyCloudProperties();
        WhyNettyRemoting whyNettyRemoting = new WhyNettyRemoting(whyNettyCommonProperties);
        whyNettyRemoting.start();

        WhyIdleStateHandler idleStateHandler = new WhyIdleStateHandler(whyKernelProperties);
        WhyConnectHandler whyConnectHandler = new WhyConnectHandler(whyKernelProperties);
        WhyMessageHandler whyMessageHandler = new WhyMessageHandler(whyNettyRemoting, whyKernelProperties);

        final WhyMessageEncoder whyMessageEncoder = new WhyMessageEncoder();
        final EventExecutorGroup group = new DefaultEventExecutorGroup(whyKernelProperties.getWorkGroup());
        new WhyKernelContainer().initHandlerRepository();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new ThreadPerTaskExecutor(new DefaultThreadFactory(KernelBossNioEventLoopGroup.class)));
        EventLoopGroup workGroup = new NioEventLoopGroup(Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2)),
                                                        new DefaultThreadFactory(KernelWorkerNioEventLoopGroup.class));
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, whyKernelProperties.getBackLog())
//              .option(ChannelOption.SO_REUSEADDR,true)
//              .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,30000)
//              .option(ChannelOption.SO_TIMEOUT,5000)
//              .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)//缓冲池
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,new WriteBufferWaterMark(whyKernelProperties.getLowWater(), whyKernelProperties.getHighWater()))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        if (whyKernelProperties.isEnableTlsSever()) {
                            ch.pipeline().addLast("sslHandler", new SslHandler(sslFactory.buildSslEngine(ch)));
                        }
                        ch.pipeline().addLast("decoder", new WhyMessageDecoder());
                        ch.pipeline().addLast("encoder", whyMessageEncoder);
                        ch.pipeline().addLast(group,"heartBeatHandler", new IdleStateHandler(whyKernelProperties.getReaderIdleTime(), 0, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(group, whyConnectHandler, whyMessageHandler, idleStateHandler);
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.bind(whyKernelProperties.getPort()).sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()){
//                    new PullConfigServerList().doCommand(topeKernelProperties);
                    log.info("Tomcat started on port(s): "+ whyKernelProperties.getPort()+" (tcp server)");
                }else {
                    log.error("Tomcat starting error: {}", future.cause().getMessage());
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(e);
        }

    }
}
