package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "转发统计对象")
public class ForwardStatus {

    @ApiModelProperty(value = "转发次数", dataType = "int")
    private Integer forwardTimes;

    @ApiModelProperty(value = "转发率", dataType = "BigDecimal")
    private BigDecimal forwardRate;
}
