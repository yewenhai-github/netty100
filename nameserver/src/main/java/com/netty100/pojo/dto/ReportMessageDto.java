package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "节点流量上报")
public class ReportMessageDto implements Serializable {

    private static final long serialVersionUID = 280725734323208831L;

    @NotNull(message = "游戏端连接数不能为空")
    @PositiveOrZero(message = "游戏端连接数必须大于等于0")
    @ApiModelProperty(value = "游戏端连接数", required = true, dataType = "int")
    private Integer platformC2pConnectActiveTotal;


    @NotNull(message = "游戏端正常断开次数不能为空")
    @PositiveOrZero(message = "游戏端正常断开次数必须大于等于0")
    @ApiModelProperty(value = "游戏端正常断开次数", required = true, dataType = "int")
    private Integer platformC2pConnectInactiveTotal;


    @NotNull(message = "游戏端异常断开次数不能为空")
    @PositiveOrZero(message = "游戏端异常断开次数必须大于等于0")
    @ApiModelProperty(value = "游戏端异常断开次数", required = true, dataType = "int")
    private Integer platformC2pConnectErrorTotal;

    @NotNull(message = "服务器连接次数不能为空")
    @PositiveOrZero(message = "服务器连接次数必须大于等于0")
    @ApiModelProperty(value = "服务器连接次数", required = true, dataType = "int")
    private Integer platformS2pConnectActiveTotal;

    @NotNull(message = "服务器正常断开次数不能为空")
    @PositiveOrZero(message = "服务器正常断开次数必须大于等于0")
    @ApiModelProperty(value = "服务器正常断开次数", required = true, dataType = "int")
    private Integer platformS2pConnectInactiveTotal;

    @NotNull(message = "服务器异常断开次数不能为空")
    @PositiveOrZero(message = "服务器异常断开次数必须大于等于0")
    @ApiModelProperty(value = "服务器异常断开次数", required = true, dataType = "int")
    private Integer platformS2pConnectErrorTotal;

    @NotNull(message = "游戏端投递成功次数不能为空")
    @PositiveOrZero(message = "游戏端投递成功次数必须大于等于0")
    @ApiModelProperty(value = "游戏端投递成功次数", required = true, dataType = "int")
    private Integer platformC2pMessageReadSuccessTotal;

    @NotNull(message = "游戏端投递失败次数不能为空")
    @PositiveOrZero(message = "游戏端投递失败次数必须大于等于0")
    @ApiModelProperty(value = "游戏端投递失败次数", required = true, dataType = "int")
    private Integer platformC2pMessageReadFailTotal;

    @NotNull(message = "服务器投递成功次数不能为空")
    @PositiveOrZero(message = "服务器投递成功次数必须大于等于0")
    @ApiModelProperty(value = "服务器投递成功次数", required = true, dataType = "int")
    private Integer platformS2pMessageReadSuccessTotal;

    @NotNull(message = "服务器投递失败次数不能为空")
    @PositiveOrZero(message = "服务器投递失败次数必须大于等于0")
    @ApiModelProperty(value = "服务器投递失败次数", required = true, dataType = "int")
    private Integer platformS2pMessageReadFailTotal;

    @NotNull(message = "消息转发次数不能为空")
    @PositiveOrZero(message = "消息转发次数必须大于等于0")
    @ApiModelProperty(value = "消息转发次数", required = true, dataType = "int")
    private Integer platformP2pMessageRelayTotal;

    @NotNull(message = "游戏端投递成功流量不能为空")
    @PositiveOrZero(message = "游戏端投递成功流量必须大于等于0")
    @ApiModelProperty(value = "游戏端投递成功流量", required = true, dataType = "long")
    private Long platformC2pMessageReadSuccessFlow;

    @NotNull(message = "游戏端投递失败流量不能为空")
    @PositiveOrZero(message = "游戏端投递失败流量必须大于等于0")
    @ApiModelProperty(value = "客户端投递失败流量", required = true, dataType = "long")
    private Long platformC2pMessageReadFailFlow;

    @NotNull(message = "服务器投递成功流量不能为空")
    @PositiveOrZero(message = "服务器投递成功流量必须大于等于0")
    @ApiModelProperty(value = "服务器投递成功流量", required = true, dataType = "long")
    private Long platformS2pMessageReadSuccessFlow;

    @NotNull(message = "服务器投递失败流量不能为空")
    @PositiveOrZero(message = "服务器投递失败流量必须大于等于0")
    @ApiModelProperty(value = "服务器投递失败流量", required = true, dataType = "long")
    private Long platformS2pMessageReadFailFlow;


    @NotNull(message = "消息转发流量不能为空")
    @PositiveOrZero(message = "消息转发流量必须大于等于0")
    @ApiModelProperty(value = "消息转发流量", required = true, dataType = "long")
    private Long platformP2pMessageRelayFlow;

    @NotNull(message = "游戏端心跳断开次数不能为空")
    @PositiveOrZero(message = "游戏端心跳断开次数必须大于等于0")
    @ApiModelProperty(value = "游戏端心跳断开次数", required = true, dataType = "int")
    private Integer platformC2pConnectIdleCloseTotal;

    @NotNull(message = "服务器心跳断开次数不能为空")
    @PositiveOrZero(message = "服务器心跳断开次数大于等于0")
    @ApiModelProperty(value = "服务器心跳断开次数", required = true, dataType = "int")
    private Integer platformS2pConnectIdleCloseTotal;

    @NotBlank(message = "节点内网地址不能为空")
    @ApiModelProperty(value = "节点内网地址", required = true, dataType = "string")
    private String intranetIp;

    @NotBlank(message = "节点端口号不能为空")
    @ApiModelProperty(value = "节点端口号", required = true, dataType = "string")
    private String port;
}
