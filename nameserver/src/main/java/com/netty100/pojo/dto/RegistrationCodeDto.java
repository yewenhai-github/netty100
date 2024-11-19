package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author why
 */
@ApiModel(value = "设备注册码参数对象")
@Getter
@Setter
public class RegistrationCodeDto {

    @NotNull(message = "设备标识不能为空")
    @ApiModelProperty(value = "设备标识")
    private Long deviceId;

    @NotNull(message = "用户标识不能为空")
    @ApiModelProperty(value = "用户标识")
    private Long userId;
}
