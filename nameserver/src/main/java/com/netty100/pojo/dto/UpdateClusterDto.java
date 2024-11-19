package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "更新集群参数相关")
public class UpdateClusterDto {

    @ApiModelProperty(value = "集群主键", required = true, dataType = "int")
    @NotNull(message = "clusterId不能为空")
    @Positive(message = "clusterId必须大于0")
    private Integer id;

    @ApiModelProperty(value = "集群描述信息", required = true, dataType = "string")
    @Size(min = 1, max = 255, message = "集群描述长度必须在[1-255]之间")
    private String description;

    @ApiModelProperty(value = "集群名称", required = true, dataType = "string")
    @Size(min = 1, max = 255, message = "集群名称长度必须在[1-255]之间")
    private String cluster;
}
