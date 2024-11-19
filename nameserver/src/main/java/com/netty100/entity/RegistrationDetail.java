package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "注册详细信息")
public class RegistrationDetail {

    @ApiModelProperty(value = "主键", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "设备标识", dataType = "long")
    private Long deviceId;

    @ApiModelProperty(value = "设备注册码", dataType = "string")
    private String devicePwd;

    @ApiModelProperty(value = "用户主键", dataType = "long")
    private Long userId;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", dataType = "string")
    private Date createTime;
}
