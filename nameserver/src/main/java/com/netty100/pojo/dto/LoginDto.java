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
@ApiModel(value = "用户登录参数")
public class LoginDto {

    @ApiModelProperty(value = "账号,账号长度必须在[1-20]之间", required = true, dataType = "string")
    @Length(min = 1, max = 20, message = "账户的长度必须在[1-20]之间")
    private String username;

    @ApiModelProperty(value = "密码,密码长度必须在[5-20]之间", required = true, dataType = "string")
    @Length(min = 5, max = 20, message = "密码的长度必须在[5-20]之间")
    private String password;
}
