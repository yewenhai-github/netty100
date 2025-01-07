package com.netty100.broker.channel;

import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.broker.devops.queue.WhyConnectQueue;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.broker.utils.WhyCountUtils;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
@ChannelHandler.Sharable
public class WhyConnectHandler extends ChannelDuplexHandler {
    private WhyKernelProperties whyKernelProperties;


    public WhyConnectHandler(WhyKernelProperties whyKernelProperties){
        this.whyKernelProperties = whyKernelProperties;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String channelKey = WhyChannelUtils.getChannelKeyByCtx(ctx);
        String channelType = WhyChannelUtils.getS2pChannelType(ctx);
        try {
            //客户端管道处理
            if(SysUtility.isNotEmpty(channelKey) && WhyChannelUtils.c2pChannelCacheDelete(ctx, channelKey)){
                //客户端管道正常关闭，通知服务器
                String[] keys = channelKey.split(IpPortBasedClient.ID_DELIMITER);
                Long userId = Long.valueOf(keys[0]);

                WhyMessage whyMsg = WhyMessageFactory.newDefaultMessage(WhyMessageCode.way_c2p_channelInactive.getCode(), -1L, ResponseCode.Rep104.getCodeBytes(), whyKernelProperties.isVariableFlag());
                whyMsg.getFixedHeader().setUserId(userId);
                whyMsg.getFixedHeader().setMessageSource(Byte.valueOf(keys[1]));
                whyMsg.getFixedHeader().setMessageDest(Byte.valueOf(keys[2]));
                WhyChannelUtils.p2sWriteAndFlush(ctx, whyMsg, whyKernelProperties.getServerCacheChannelReTimes());

                //客户端管道正常关闭，数据统计
                WhyCountUtils.platform_c2p_connect_inactive_total.add(1);
                WhyConnectQueue.pushClientChannelInactiveQueue(ctx, userId, channelKey);
                WhyConnectQueue.pushClientChannelLogQueue(ctx, whyMsg, LogPointCode.C02.getCode(), LogPointCode.C02.getMessage());
                return;
            }
            //服务器管道处理
            if(SysUtility.isNotEmpty(channelType)){
                if(WhyChannelUtils.s2pChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_inactive_total.add(1);
                    WhyConnectQueue.pushServerChannelInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C51.getCode(), LogPointCode.C51.getMessage());
                }else if(WhyChannelUtils.s2pJobChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_inactive_total.add(1);
                    WhyConnectQueue.pushServerChannelInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C52.getCode(), LogPointCode.C52.getMessage());
                }else if(WhyChannelUtils.s2pMqChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_inactive_total.add(1);
                    WhyConnectQueue.pushServerChannelInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C53.getCode(), LogPointCode.C53.getMessage());
                }else if(WhyChannelUtils.simplexChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_inactive_total.add(1);
                    WhyConnectQueue.pushServerChannelInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C60.getCode(), LogPointCode.C60.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("ctx close error channelInactive,channelKey={},channelType={},channelId={}", channelKey, channelType, ctx.channel().id(), e);
        } finally {
            ctx.close();
        }
    }

    /**
     * 异常捕获处理
     * Channel中所有未捕获的异常,都会触发exceptionCaught方法,比如后台返回的数据格式不对,产生了没有捕获的异常,就会触发exceptionCaught,
     * 所以,在ChannelInboundHandlerAdapter的各种方法中,最好不要有没有处理的业务逻辑异常.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String channelKey = WhyChannelUtils.getChannelKeyByCtx(ctx);
        String channelType = WhyChannelUtils.getS2pChannelType(ctx);
        try {
            //客户端管道处理
            if(SysUtility.isNotEmpty(channelKey) && WhyChannelUtils.c2pChannelCacheDelete(ctx, channelKey)){
                //客户端管道异常关闭，通知服务器
                String[] keys = channelKey.split(IpPortBasedClient.ID_DELIMITER);
                Long userId = Long.valueOf(keys[0]);

                WhyMessage whyMsg = WhyMessageFactory.newDefaultMessage(WhyMessageCode.way_c2p_exceptionCaught.getCode(), -1L, ResponseCode.Rep105.getCodeBytes(), whyKernelProperties.isVariableFlag());
                whyMsg.getFixedHeader().setUserId(userId);
                whyMsg.getFixedHeader().setMessageSource(Byte.valueOf(keys[1]));
                whyMsg.getFixedHeader().setMessageDest(Byte.valueOf(keys[2]));
                WhyChannelUtils.p2sWriteAndFlush(ctx, whyMsg, whyKernelProperties.getServerCacheChannelReTimes());

                //客户端管道异常关闭，数据统计
                WhyCountUtils.platform_c2p_connect_error_total.add(1);
                WhyConnectQueue.pushClientChannelExceptionCaughtQueue(ctx, userId, channelKey);
                WhyConnectQueue.pushClientChannelLogQueue(ctx, whyMsg, LogPointCode.C03.getCode(), LogPointCode.C03.getMessage());
                return;
            }
            //服务器管道处理
            if(WhyChannelUtils.s2pChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                WhyCountUtils.platform_s2p_connect_error_total.add(1);
                WhyConnectQueue.pushServerChannelExceptionCaughtQueue(ctx, channelType);
                WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C54.getCode(), LogPointCode.C54.getMessage());
            }else if(WhyChannelUtils.s2pJobChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                WhyCountUtils.platform_s2p_connect_error_total.add(1);
                WhyConnectQueue.pushServerChannelExceptionCaughtQueue(ctx, channelType);
                WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C55.getCode(), LogPointCode.C55.getMessage());
            }else if(WhyChannelUtils.s2pMqChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                WhyCountUtils.platform_s2p_connect_error_total.add(1);
                WhyConnectQueue.pushServerChannelExceptionCaughtQueue(ctx, channelType);
                WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C56.getCode(), LogPointCode.C56.getMessage());
            }else if(WhyChannelUtils.simplexChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                WhyCountUtils.platform_s2p_connect_error_total.add(1);
                WhyConnectQueue.pushServerChannelExceptionCaughtQueue(ctx, channelType);
                WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C61.getCode(), LogPointCode.C61.getMessage());
            }
        } catch (Exception e) {
            log.error("ctx close error exceptionCaught,channelKey={},channelType={},channelId={}", channelKey, channelType, ctx.channel().id(), e);
        } finally {
            ctx.close();
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
    }
}