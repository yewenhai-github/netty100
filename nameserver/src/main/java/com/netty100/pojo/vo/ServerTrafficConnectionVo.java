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
@ApiModel(value = "告警页面连接分钟及数据")
public class ServerTrafficConnectionVo {

    @ApiModelProperty(value = "游戏端连接次数")
    private Integer platformC2pConnectActiveTotal;

    @ApiModelProperty(value = "游戏端正常断开次数")
    private Integer platformC2pConnectInactiveTotal;

    @ApiModelProperty(value = "游戏端异常断开次数")
    private Integer platformC2pConnectErrorTotal;

    @ApiModelProperty(value = "游戏端心跳断开次数")
    private Integer platformC2pConnectIdleCloseTotal;

    @ApiModelProperty(value = "服务器连接次数")
    private Integer platformS2pConnectActiveTotal;

    @ApiModelProperty(value = "服务器正常断开次数")
    private Integer platformS2pConnectInactiveTotal;

    @ApiModelProperty(value = "服务器异常断开次数")
    private Integer platformS2pConnectErrorTotal;

    @ApiModelProperty(value = "服务器心跳断开次数")
    private Integer platformS2pConnectIdleCloseTotal;

    @ApiModelProperty(value = "创建时间 HH:MM")
    private String createTime;
}
