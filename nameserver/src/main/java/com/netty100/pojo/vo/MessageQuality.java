package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "消息投递次数统计结果")
public class MessageQuality {

    @ApiModelProperty(value = "游戏端总投递次数")
    private Integer clientTotalSendTimes;

    @ApiModelProperty(value = "游戏端投递成功次数")
    private Integer clientSuccessSendTimes;

    @ApiModelProperty(value = "服务器总投递次数")
    private Integer serverTotalSendTimes;

    @ApiModelProperty(value = "服务器投递成功次数")
    private Integer serverSuccessSendTimes;
}
