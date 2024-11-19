package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author why
 */
@Data
@ApiModel(value = "告警增长率")
public class ReportWarnStatisticsRate {

    @JsonIgnore
    @ApiModelProperty(value = "主键", dataType = "int")
    private Integer id;

    @JsonIgnore
    @ApiModelProperty(value = "集群id", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "增长率", dataType = "BigDecimal")
    private BigDecimal increaseRate;

    @ApiModelProperty(value = "增长率计算日期 格式yyyy-MM-dd", dataType = "string")
    private String calcDay;
}