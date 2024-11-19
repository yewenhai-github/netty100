package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class ClientConnectVo {

    @ApiModelProperty(value = "游戏端连接次数")
    private Integer platformC2pConnectActiveTotal;

    @ApiModelProperty(value = "游戏端正常断开次数")
    private Integer platformC2pConnectInactiveTotal;

    @ApiModelProperty(value = "游戏端异常断开次数")
    private Integer platformC2pConnectErrorTotal;

    @ApiModelProperty(value = "游戏端心跳断开次数")
    private Integer platformC2pConnectIdleCloseTotal;

    @ApiModelProperty(value = "创建时间 HH:MM")
    private String createTime;
}
