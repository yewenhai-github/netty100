package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "客户端获取节点列表参数")
public class ClientServerQueryDto {

    @NotBlank(message = "集群名称不能为空")
    @ApiModelProperty(value = "集群名称")
    private String cluster;
}
