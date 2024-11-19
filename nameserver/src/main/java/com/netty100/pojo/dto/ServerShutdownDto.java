package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "停机上报参数")
public class ServerShutdownDto {

    @NotBlank(message = "内网地址不能为空")
    @ApiModelProperty(value = "内网IP地址", required = true, dataType = "string")
    private String intranetIp;

    @NotBlank(message = "端口号不能为空")
    @ApiModelProperty(value = "端口号", required = true, dataType = "string")
    private String port;
}
