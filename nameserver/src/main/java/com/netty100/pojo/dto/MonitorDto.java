package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "告警页面参数相关")
public class MonitorDto {

    @NotNull(message = "集群主键不能为空")
    @ApiModelProperty(value = "集群主键", dataType = "int", required = true)
    private Integer clusterId;

    @ApiModelProperty(value = "节点主键", dataType = "int")
    private Integer serverId;
}
