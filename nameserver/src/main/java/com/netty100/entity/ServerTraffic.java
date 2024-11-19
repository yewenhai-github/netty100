package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Getter
@Setter
@ApiModel(value = "流量连接")
public class ServerTraffic implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "netty服务器流量记录主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "netty节点主键", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "集群主键", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "游戏端连接次数", dataType = "int")
    private Integer platformC2pConnectActiveTotal;

    @ApiModelProperty(value = "游戏端正常断开次数", dataType = "int")
    private Integer platformC2pConnectInactiveTotal;

    @ApiModelProperty(value = "游戏端异常断开次数", dataType = "int")
    private Integer platformC2pConnectErrorTotal;

    @ApiModelProperty(value = "游戏端心跳断开次数", dataType = "int")
    private Integer platformC2pConnectIdleCloseTotal;

    @ApiModelProperty(value = "服务器连接次数", dataType = "int")
    private Integer platformS2pConnectActiveTotal;

    @ApiModelProperty(value = "服务器正常断开次数", dataType = "int")
    private Integer platformS2pConnectInactiveTotal;

    @ApiModelProperty(value = "服务器异常断开次数", dataType = "int")
    private Integer platformS2pConnectErrorTotal;

    @ApiModelProperty(value = "服务器心跳断开次数", dataType = "int")
    private Integer platformS2pConnectIdleCloseTotal;

    @ApiModelProperty(value = "游戏端投递成功次数", dataType = "int")
    private Integer platformC2pMessageReadSuccessTotal;

    @ApiModelProperty(value = "游戏端投递失败次数", dataType = "int")
    private Integer platformC2pMessageReadFailTotal;

    @ApiModelProperty(value = "服务器投递成功次数", dataType = "int")
    private Integer platformS2pMessageReadSuccessTotal;

    @ApiModelProperty(value = "服务器投递失败次数", dataType = "int")
    private Integer platformS2pMessageReadFailTotal;

    @ApiModelProperty(value = "消息转发次数", dataType = "int")
    private Integer platformP2pMessageRelayTotal;

    @ApiModelProperty(value = "游戏端投递成功流量", dataType = "long")
    private Long platformC2pMessageReadSuccessFlow;

    @ApiModelProperty(value = "游戏端投递失败流量", dataType = "long")
    private Long platformC2pMessageReadFailFlow;

    @ApiModelProperty(value = "服务器投递成功流量", dataType = "long")
    private Long platformS2pMessageReadSuccessFlow;

    @ApiModelProperty(value = "服务器投递失败流量", dataType = "long")
    private Long platformS2pMessageReadFailFlow;

    @ApiModelProperty(value = "消息转发流量", dataType = "long")
    private Long platformP2pMessageRelayFlow;

    @ApiModelProperty(value = "创建日期",dataType = "string")
    private String createDate;

    @ApiModelProperty(value = "创建日期",dataType = "string")
    private String createTime;
}
