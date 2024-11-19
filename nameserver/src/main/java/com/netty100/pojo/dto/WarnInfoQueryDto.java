package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netty100.enumeration.WarnType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "告警信息查询参数相关")
public class WarnInfoQueryDto {

    @NotNull(message = "节点逐渐不能为空")
    @ApiModelProperty(value = "节点主键", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "告警类型", dataType = "string")
    private WarnType warnType;

    @NotNull(message = "查询开始时间不能为空")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询开始时间", dataType = "string")
    private Date begin;

    @NotNull(message = "查询结束时间不能为空")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询结束时间", dataType = "string")
    private Date end;

    @ApiModelProperty(value = "第几页,默认为第一页", dataType = "int")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10", dataType = "int")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空",dataType = "string")
    private String orderBy = "";

    @ApiModelProperty(value = "排序方式,可为asc,desc,默认为asc",dataType = "string")
    private String orderFlag = "asc";
}
