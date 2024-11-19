package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "集群用户关联参数相关")
public class AssignClusterToUserDto {


    @ApiModelProperty(value = "集群主键", required = true, dataType = "int")
    @NotNull(message = "集群主键不能为空")
    @Positive(message = "集群主键必须大于0")
    private Integer clusterId;

    @ApiModelProperty(value = "关联的用户主键", required = true, dataType = "string")
    @NotBlank(message = "用户名称不能为空,如果需要分配给多个用户,id之间以英文逗号分割")
    private String userIds;
}
