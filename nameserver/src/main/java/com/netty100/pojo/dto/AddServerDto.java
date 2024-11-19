package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "netty节点添加参数相关")
public class AddServerDto {

    @NotNull(message = "clusterId值不能为空")
    @Positive(message = "clusterId值必须大于0")
    @ApiModelProperty(value = "集群id", required = true, dataType = "int")
    private Integer clusterId;

    @NotBlank(message = "外网地址不能为空")
    @ApiModelProperty(value = "外网IP地址", required = true, dataType = "string")
    private String extranetIp;

    @NotBlank(message = "内网地址不能为空")
    @ApiModelProperty(value = "内网IP地址", required = true, dataType = "string")
    private String intranetIp;

    @NotBlank(message = "端口号不能为空")
    @ApiModelProperty(value = "端口号", required = true, dataType = "string")
    private String port;
}
