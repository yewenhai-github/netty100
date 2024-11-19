package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(description = "用户更新参数")
public class UpdateUserDto {

    @ApiModelProperty(value = "用户类型,普通用户->0,管理员->1", dataType = "int")
    private Integer userType;

    @ApiModelProperty(value = "邮箱", dataType = "string")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "钉钉手机号码", dataType = "string")
    @Size(min = 11, max = 20, message = "钉钉手机号码的长度必须在[11-20]之间")
    private String dingTalk;

    @ApiModelProperty(value = "是否接收告警信息,不接受告警信息->0,接收告警->1", dataType = "int")
    private Integer acceptWarn;

    @NotNull(message = "主键信息不能为空")
    @Positive(message = "id值必须大于0")
    @ApiModelProperty(value = "用户主键", dataType = "int", required = true)
    private Integer id;
}
