package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author why
 */
@Data
public class ReportWarnStatistics {

    @ApiModelProperty(value = "主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "集群id", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "netty服务器id,预留字段", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "告警类型", dataType = "int")
    private Integer warnType;

    @ApiModelProperty(value = "预警发生日期 格式yyyy-MM-dd", dataType = "string")
    private String statisticsDay;

    @ApiModelProperty(value = "告警数量", dataType = "int")
    private Integer count;

    @ApiModelProperty(value = "创建时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}