package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "统计结果查询参数相关")
public class StatisticsQueryDto {

    @NotNull(message = "集群id不能为空")
    @ApiModelProperty(value = "集群id", dataType = "int", required = true)
    private Integer clusterId;

    @NotBlank(message = "查询日期不能为空")
    @ApiModelProperty(value = "查询日期,数据格式为yyyy-MM-dd", dataType = "string", required = true)
    private String date;

    @ApiModelProperty(value = "用户id", dataType = "int", hidden = true)
    private Integer userId;
}
