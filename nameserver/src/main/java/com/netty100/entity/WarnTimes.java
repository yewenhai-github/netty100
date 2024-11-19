package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
@ApiModel(value = "告警次数对象")
public class WarnTimes implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "已告警次数", dataType = "int")
    private Integer times;

    @ApiModelProperty(value = "更新时间戳", dataType = "long")
    private Long updateTime;

    @ApiModelProperty(value = "告警键,告警类型-接收告警信息的人员主键", dataType = "string")
    private String key;
}
