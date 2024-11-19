package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class ServerConnectVo {

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
