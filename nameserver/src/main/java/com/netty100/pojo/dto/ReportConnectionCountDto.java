package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "心跳连接信息上报")
public class ReportConnectionCountDto {

    @NotBlank(message = "内网地址不能为空")
    @ApiModelProperty(value = "内网地址", required = true, dataType = "string")
    private String intranetIp;

    @NotBlank(message = "绑定端口号不能为空")
    @ApiModelProperty(value = "绑定端口号", required = true, dataType = "string")
    private String port;

    @NotNull(message = "游戏端连接数不能为空")
    @PositiveOrZero(message = "游戏端连接数必须大于等于0")
    @ApiModelProperty(value = "游戏端连接数", required = true, dataType = "int")
    private Integer clientConnectCount;

    @NotNull(message = "服务器连接数不能为空")
    @PositiveOrZero(message = "服务器连接数必须大于等于0")
    @ApiModelProperty(value = "服务器连接数", required = true, dataType = "int")
    private Integer serverConnectCount;
}
