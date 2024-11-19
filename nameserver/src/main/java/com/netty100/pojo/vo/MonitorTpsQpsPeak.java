package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "监控tps-qps峰值")
public class MonitorTpsQpsPeak {

    @ApiModelProperty(value = "c2pTps峰值", dataType = "int")
    private Integer c2pTpsPeak;

    @ApiModelProperty(value = "p2sTps峰值", dataType = "int")
    private Integer p2sTpsPeak;

    @ApiModelProperty(value = "s2pTps峰值", dataType = "int")
    private Integer s2pTpsPeak;

    @ApiModelProperty(value = "p2cTps峰值", dataType = "int")
    private Integer p2cTpsPeak;

    @ApiModelProperty(value = "c2pQps峰值", dataType = "int")
    private Integer c2pQpsPeak;

    @ApiModelProperty(value = "p2sQps峰值", dataType = "int")
    private Integer p2sQpsPeak;

    @ApiModelProperty(value = "s2pQps峰值", dataType = "int")
    private Integer s2pQpsPeak;

    @ApiModelProperty(value = "p2cQps峰值", dataType = "int")
    private Integer p2cQpsPeak;
}
