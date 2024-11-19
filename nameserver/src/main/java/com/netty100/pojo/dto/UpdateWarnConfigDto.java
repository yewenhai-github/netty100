package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "预警阈值修改参数相关")
public class UpdateWarnConfigDto {

    @NotNull(message = "主键不能为空")
    @ApiModelProperty(value = "主键")
    private Integer id;

    @NotNull(message = "告警阈值不能为空")
    @ApiModelProperty("对应告警阈值")
    private BigDecimal typeThreshold;
}
