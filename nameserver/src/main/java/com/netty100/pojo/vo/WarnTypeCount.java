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
@ApiModel(value = "告警类型次数对象")
public class WarnTypeCount {

    @ApiModelProperty(value = "告警类型名称",dataType = "string")
    private String warnTypeName;

    @ApiModelProperty(value = "告警次数",dataType = "int")
    private Integer times;

    @ApiModelProperty(value = "占比",dataType = "BigDecimal")
    private BigDecimal rate;
}
