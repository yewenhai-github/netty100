package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author why
 */
@Getter
@Setter
@ToString
@ApiModel(value = "在线调试参数")
public class OnlineDebugDto {

    private int type = Integer.MAX_VALUE;

    @NotBlank(message = "netty节点连接地址不能为空")
    @ApiModelProperty(value = "netty节点连接地址", required = true)
    private String localAddress;

    @NotBlank(message = "netty节点连接端口号不能为空")
    @ApiModelProperty(value = "netty节点连接端口号", required = true)
    private String localPort;

    @NotNull(message = "消息方式不能为空")
    @ApiModelProperty(value = "消息方式", required = true)
    private Byte messageWay;

    @NotNull(message = "消息源不能为空")
    @ApiModelProperty(value = "消息源", required = true)
    private Byte messageSource;

    @NotNull(message = "消息目的地不能为空")
    @ApiModelProperty(value = "消息目的地", required = true)
    private Byte messageDest;

    @NotNull(message = "消息类型不能为空")
    @ApiModelProperty(value = "消息类型", required = true)
    private Byte messageType;

    @NotNull(message = "消息序列化方式不能为空")
    @ApiModelProperty(value = "消息序列化方式", required = true)
    private Byte messageSerialize;

    @NotNull(message = "消息标识不能为空")
    @ApiModelProperty(value = "消息标识", required = true)
    private Long messageId;

    @NotNull(message = "设备标识不能为空")
    @ApiModelProperty(value = "设备标识", required = true)
    private Long deviceId;

    @NotNull(message = "用户标识不能为空")
    @ApiModelProperty(value = "用户标识", required = true)
    private Long userId;

    @ApiModelProperty(value = "发送消息", required = true)
    private String message;
}
