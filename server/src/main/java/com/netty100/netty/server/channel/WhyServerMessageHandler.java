package com.netty100.netty.server.channel;

import com.netty100.common.constants.CommonConstants;
import com.netty100.common.properties.WhyNettyCommonProperties;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.producer.WhyMessageProducerService;
import com.netty100.netty.server.utils.WhyServerCountUtils;
import com.netty100.netty.server.utils.WhyServerUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
@ChannelHandler.Sharable
public class WhyServerMessageHandler extends SimpleChannelInboundHandler {
    private WhyNettyCommonProperties whyNettyCommonProperties;
    private WhyMessageProducerService whyMessageProducerService;

    /**
     * 客户端消息解析
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        WhyMessage whyMsg = (WhyMessage)msg;
        try {
            SysUtility.setCurrentProducerChannel(ctx.channel());
            SysUtility.setCurrentClientMessageId(whyMsg.getFixedHeader().getMessageId());

            switch (whyMsg.getFixedHeader().getMessageWay()){
                case CommonConstants.way_c2p_channelActive:
                    WhyServerUtils.channelActive(whyMsg.getFixedHeader().getUserId(), ctx);
                    break;
                case CommonConstants.way_c2p_channelInactive:
                case CommonConstants.way_c2p_exceptionCaught:
                case CommonConstants.way_c2p_idleState:
                    WhyServerUtils.channelInactive(whyMsg.getFixedHeader().getUserId(), ctx);
                    break;
                case CommonConstants.way_c2p_channelRead0:
                case CommonConstants.way_s2p_channelRead0:
                case CommonConstants.way_simplex_channelRead0:
                    //1.总请求数收集
                    WhyServerCountUtils.countTotalRead.incrementAndGet();
                    //2.数据处理
                    WhyServerUtils.doCommand(whyMsg.getPayload(), whyMsg.getFixedHeader(), ctx.channel());
                    //3.成功请求数收集
                    WhyServerCountUtils.countSuccessRead.incrementAndGet();
                    break;
                default:
                    //TODO 不支持的消息类型
                    break;
            }
        }catch (Exception e) {
            log.error("sdk兜底策略触发,用户{} 报错", whyMsg.getFixedHeader().getUserId(), e);
            if(SysUtility.isEmpty(whyMessageProducerService)){
                whyMessageProducerService = WhySpringUtils.getBean(WhyMessageProducerService.class);
            }
            if(SysUtility.isEmpty(whyNettyCommonProperties)){
                whyNettyCommonProperties = WhySpringUtils.getBean(WhyNettyCommonProperties.class);
            }
            String content = ResponseCode.Rep300.getCode()+"-"+ ResponseCode.Rep300.getMassage()+"："+ SysUtility.getErrorMsg(e);

            whyMessageProducerService.sendMessage(ctx.channel(),
                    whyNettyCommonProperties.getMessageWay(),
                    whyNettyCommonProperties.getMessageSource(),
                    whyNettyCommonProperties.getMessageDest(),
                    WhyMessageCode.serialize_string.getCode(),
                    whyMsg.getFixedHeader().getUserId(),
                    content.getBytes());
        } finally {
            ctx.flush();
            SysUtility.destoryCurrentInheritableThreadLocal();
        }
    }
}



