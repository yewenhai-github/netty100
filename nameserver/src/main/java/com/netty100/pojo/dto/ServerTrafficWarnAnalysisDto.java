package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/1
 */
@Data
public class ServerTrafficWarnAnalysisDto {

    @ApiModelProperty(value = "游戏端连接次数")
    private Integer platformC2pConnectActiveCount;

    @ApiModelProperty(value = "游戏端异常断开次数")
    private Integer platformC2pConnectErrorTimes;

    @ApiModelProperty(value = "服务器异常断开次数")
    private Integer platformS2pConnectErrorTimes;

    @ApiModelProperty(value = "游戏端投递失败次数")
    private Integer platformC2pMessageReadFailTimes;

    @ApiModelProperty(value = "游戏端投递失败流量")
    private Long platformC2pMessageReadFailFlow;

    @ApiModelProperty(value = "服务器投递失败次数")
    private Integer platformS2pMessageReadFailTotal;

    @ApiModelProperty(value = "服务器投递失败流量")
    private Long platformS2pMessageReadFailFlow;

    @ApiModelProperty(value = "消息转发次数 ")
    private Integer platformP2pMessageRelayTimes;

    @ApiModelProperty(value = "重连次数")
    private Integer reconnectTimes;

    @ApiModelProperty(value = "消息发送失败率")
    private BigDecimal failedMessageRate;

    @ApiModelProperty(value = "消息转发率")
    private BigDecimal forwardMessageRate;
}
