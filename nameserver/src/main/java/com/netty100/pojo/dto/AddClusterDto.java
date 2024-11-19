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
@ApiModel(value = "添加集群参数相关")
public class AddClusterDto {

    @ApiModelProperty(value = "集群名称", required = true, dataType = "string")
    @Length(min = 1, max = 20, message = "集群名称长度必须在[1-20]之间")
    private String cluster;

    @ApiModelProperty(value = "集群描述信息,描述信息不能为空,长度在[1-255]", required = true, dataType = "string")
    @Length(min = 1, max = 255, message = "集群描述长度必须在[1-255]之间")
    private String description;
}
