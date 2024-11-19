package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "消息发送成功率")
public class MessageRateVo {

    @ApiModelProperty(value = "游戏端消息失败次数")
    private Long clientFailedTimes;

    @ApiModelProperty(value = "游戏端总消息次数")
    private Long clientSendTimes;

    @ApiModelProperty(value = "游戏端消息失败率")
    private BigDecimal clientFailedRate;

    @ApiModelProperty(value = "服务器端消息失败次数 ")
    private Long serverFailedTimes;

    @ApiModelProperty(value = "服务器端消息总次数")
    private Long serverSendTimes;

    @ApiModelProperty(value = "服务器端消息失败率")
    private BigDecimal serverFailedRate;
}
