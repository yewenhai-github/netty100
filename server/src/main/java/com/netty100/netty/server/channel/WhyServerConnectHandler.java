package com.netty100.netty.server.channel;

import com.netty100.common.constants.CommonConstants;
import com.netty100.common.properties.WhyNettyCommonProperties;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.server.properties.WhyNettyServerProperties;
import com.netty100.netty.server.utils.WhyServerCountUtils;
import com.netty100.netty.server.utils.WhyServerUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
@ChannelHandler.Sharable
public class WhyServerConnectHandler extends ChannelDuplexHandler {
    private WhyNettyCommonProperties whyNettyCommonProperties;
    private WhyNettyServerProperties whyNettyServerProperties;
    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            if(SysUtility.isEmpty(whyNettyCommonProperties)){
                whyNettyCommonProperties = WhySpringUtils.getBean(WhyNettyCommonProperties.class);
            }
            if(SysUtility.isEmpty(whyNettyServerProperties)){
                whyNettyServerProperties = WhySpringUtils.getBean(WhyNettyServerProperties.class);
            }

            WhyServerCountUtils.countTotalWrite.incrementAndGet();

            byte messageWay = WhyMessageCode.way_s2p_channelActive.getCode();
            //1.0.3版本新增一套通用的报文：simplex
            if(whyNettyServerProperties.getTransferMode() == CommonConstants.TRANSFER_MODE_SIMPLEX){
                messageWay = WhyMessageCode.way_simplex_channelActive.getCode();
            }
            //1.0.2以前版本的报文：MQ、JOB
            if(whyNettyServerProperties.getClientType() == CommonConstants.CLIENT_TYPE_JOB){
                messageWay = CommonConstants.way_j2p_channelActive;
            }else if(whyNettyServerProperties.getClientType() == CommonConstants.CLIENT_TYPE_MQ){
                messageWay = CommonConstants.way_m2p_channelActive;
            }

            ctx.writeAndFlush(WhyMessageFactory.newDefaultMessage(messageWay,
                    whyNettyCommonProperties.getMessageSource(),
                    whyNettyCommonProperties.getMessageDest(),
                    -1L, null, false).bytes());
            WhyServerCountUtils.countSuccessWrite.incrementAndGet();
            WhyServerCountUtils.connectTotal.incrementAndGet();

            WhyServerUtils.channelCache(ctx);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if(WhyServerUtils.getChannelIdMap().containsKey(ctx.channel().id())){
            WhyServerCountUtils.connectTotal.decrementAndGet();
        }
        String channelKey = WhyServerUtils.closeCtx(ctx.channel());
        if(SysUtility.isEmpty(channelKey)){
            return;
        }
//        log.error("Server 远程正常断开，channelKey={},channelId={}",channelKey , ctx.channel().id());
    }

    /**
     * 异常捕获处理
     * Channel中所有未捕获的异常,都会触发exceptionCaught方法,比如后台返回的数据格式不对,产生了没有捕获的异常,就会触发exceptionCaught,
     * 所以,在ChannelInboundHandlerAdapter的各种方法中,最好不要有没有处理的业务逻辑异常.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(WhyServerUtils.getChannelIdMap().containsKey(ctx.channel().id())){
            WhyServerCountUtils.connectTotal.decrementAndGet();
        }
        String channelKey = WhyServerUtils.closeCtx(ctx.channel());
        if(SysUtility.isEmpty(channelKey)){
            return;
        }
//        log.error("Server 远程异常断开，channelKey={},channelId={},异常={}",channelKey , ctx.channel().id(), cause);
    }


    public void logMsgPoint(String point, WhyMessage topeMsg){

        log.info(point+":UserId={},Id={},Type={},Way={},Serialize={},Source={},Dest={}",
                topeMsg.getFixedHeader().getUserId(),
                topeMsg.getFixedHeader().getMessageId(),
                topeMsg.getFixedHeader().getMessageType(),
                topeMsg.getFixedHeader().getMessageWay(),
                topeMsg.getFixedHeader().getMessageSerialize(),
                topeMsg.getFixedHeader().getMessageSource(),
                topeMsg.getFixedHeader().getMessageDest());
    }
}



