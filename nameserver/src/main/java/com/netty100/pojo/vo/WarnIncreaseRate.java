package com.netty100.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel(value = "告警增长率")
public class WarnIncreaseRate {

    @ApiModelProperty(value = "日期")
    private String createDate;

    @ApiModelProperty(value = "告警增长率")
    private BigDecimal increaseRate;

    @JsonIgnore
    @ApiModelProperty(value = "告警总次数", hidden = true)
    private Integer total;
}
