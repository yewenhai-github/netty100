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
@ApiModel(value = "新增消息协议参数相关")
public class AddProtocolDto {

    @NotBlank(message = "协议代码值不能为空")
    @ApiModelProperty(value = "协议代码值", required = true, dataType = "string")
    private String protocolCode;

    @NotBlank(message = "协议名称不能为空")
    @ApiModelProperty(value = "协议名称", required = true, dataType = "string")
    private String protocolName;

    @NotBlank(message = "描述信息不能为空")
    @ApiModelProperty(value = "描述信息", required = true, dataType = "string")
    private String protocolDesc;

    @NotBlank(message = "消息协议类型不能为空")
    @ApiModelProperty(value = "消息协议类型,消息类型,消息源,消息目的地,消息方式", required = true, dataType = "string")
    private String protocolType;
}
