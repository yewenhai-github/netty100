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
@ApiModel(value = "用户分页查询参数相关")
public class UserQueryDto {

    @ApiModelProperty(value = "账户名称", dataType = "string")
    private String username;

    @ApiModelProperty(value = "用户类型", dataType = "int")
    private Integer userType;

    @ApiModelProperty(value = "第几页,默认为第一页", dataType = "int")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10", dataType = "int")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空", dataType = "string")
    private String orderBy = "create_time";

    @ApiModelProperty(value = "排序方式,可选值为desc,asc,默认为asc", dataType = "string")
    private String orderFlag = "asc";
}
