package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author why
 * @since 2022-03-30
 */

@Data
@ApiModel(value = "告警配置")
public class WarnConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", dataType = "int",hidden = true)
    private Integer id;

    @ApiModelProperty(value = "server主键信息,可以为0,表示默认配置", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "对应告警枚举类index值", dataType = "int")
    private Integer typeIndex;

    @ApiModelProperty(value = "对应告警枚举类title值", dataType = "string")
    private String typeTitle;

    @ApiModelProperty(value = "组名称", dataType = "string")
    private String typeGroup;

    @ApiModelProperty(value = "对应告警阈值", dataType = "BigDecimal")
    private BigDecimal typeThreshold;
}
