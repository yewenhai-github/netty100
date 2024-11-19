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
@ApiModel(value = "节点列表查询参数")
public class ServerListDto {

    @NotNull(message = "集群主键不能为空")
    @ApiModelProperty(value = "集群主键", dataType = "int", required = true)
    private Integer clusterId;

    @ApiModelProperty(value = "第几页,默认为第一页")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空")
    private String orderBy = "create_time";

    @ApiModelProperty(value = "排序方式,asc或者desc,默认为asc")
    private String orderFlag = "desc";
}
