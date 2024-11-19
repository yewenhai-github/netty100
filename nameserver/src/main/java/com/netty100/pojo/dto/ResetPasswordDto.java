package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "重置密码参数")
public class ResetPasswordDto {

    @ApiModelProperty(value = "新密码",required = true,dataType = "string")
    @Length(min = 5, max = 20, message = "密码的长度必须在[5-20]之间")
    private String newPassword;
}
