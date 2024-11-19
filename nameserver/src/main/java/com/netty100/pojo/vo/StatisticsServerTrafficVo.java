package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class StatisticsServerTrafficVo {

    @ApiModelProperty(value = "游戏端投递成功流量")
    private Long platformC2pMessageReadSuccessFlow;

    @ApiModelProperty(value = "游戏端投递失败流量")
    private Long platformC2pMessageReadFailFlow;

    @ApiModelProperty(value = "服务器投递成功流量")
    private Long platformS2pMessageReadSuccessFlow;

    @ApiModelProperty(value = "服务器投递失败流量")
    private Long platformS2pMessageReadFailFlow;

    @ApiModelProperty(value = "消息转发流量")
    private Long platformP2pMessageRelayFlow;

    @ApiModelProperty(value = "创建时间 HH:MM")
    private String createTime;
}
