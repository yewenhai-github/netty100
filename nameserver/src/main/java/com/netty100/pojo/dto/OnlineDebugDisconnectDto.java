package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "连接断开参数对象")
public class OnlineDebugDisconnectDto {

    @NotNull(message = "设备标识不能为空")
    @ApiModelProperty(value = "设备标识", required = true)
    private Long deviceId;

    @NotNull(message = "用户标识不能为空")
    @ApiModelProperty(value = "用户标识", required = true)
    private Long userId;

    @NotBlank(message = "netty节点连接地址不能为空")
    @ApiModelProperty(value = "netty节点连接地址", required = true)
    private String localAddress;

    @NotBlank(message = "netty节点连接端口号不能为空")
    @ApiModelProperty(value = "netty节点连接端口号", required = true)
    private String localPort;

}
