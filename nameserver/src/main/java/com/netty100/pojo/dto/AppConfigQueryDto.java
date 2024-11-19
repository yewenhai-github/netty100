package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "应用协议配置查询对象")
public class AppConfigQueryDto {

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "集群名称")
    private String cluster;

    @ApiModelProperty(value = "第几页,默认为第一页")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空")
    private String orderBy = "create_time";

    @ApiModelProperty(value = "排序方式,默认为降序")
    private String orderFlag = "desc";
}
