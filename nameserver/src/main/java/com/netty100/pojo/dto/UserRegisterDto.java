package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "添加用户参数")
public class UserRegisterDto {

    @ApiModelProperty(value = "账户名,账户名的长度必须在[1-20]之间", required = true, dataType = "string")
    @Length(min = 1, max = 20, message = "账户的长度必须在[1-20]之间")
    private String username;

    @ApiModelProperty(value = "密码,密码的长度必须在[5-20]之间", required = true, dataType = "string")
    @Length(min = 5, max = 20, message = "密码的长度必须在[5-20]之间")
    private String password;

    @NotNull(message = "用户类型不能为空")
    @ApiModelProperty(value = "用户类型,普通用户->0,管理员->1", required = true, dataType = "int")
    private Integer userType;

    @ApiModelProperty(value = "邮箱", dataType = "string")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "钉钉手机号码,钉钉手机号码的长度必须在[11-15]之间", dataType = "string")
    @Size(min = 11, max = 15, message = "钉钉手机号码的长度必须在[11-15]之间")
    private String dingTalk;

    @NotNull(message = "接收预警信息状态不能为空")
    @ApiModelProperty(value = "是否接收告警信息,不接受告警->0,接收告警->1", required = true, dataType = "int")
    private Integer acceptWarn;
}
