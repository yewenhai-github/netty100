package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Getter
@Setter
@ApiModel(value = "系统用户")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "账户名称", dataType = "string")
    private String username;

    @JsonIgnore
    @ApiModelProperty(value = "账户密码", dataType = "string")
    private String password;

    @ApiModelProperty(value = "用户类型", dataType = "int")
    private Integer userType;

    private String userTypeDesc;

    @ApiModelProperty(value = "邮箱", dataType = "string")
    private String email;

    @ApiModelProperty(value = "钉钉号码", dataType = "string")
    private String dingTalk;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上一次登录时间", dataType = "string")
    private Date lastLoginTime;

    @ApiModelProperty(value = "是否接收告警信息", dataType = "int")
    private Integer acceptWarn;

    private String acceptWarnDesc;

    @ApiModelProperty(value = "创建时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
