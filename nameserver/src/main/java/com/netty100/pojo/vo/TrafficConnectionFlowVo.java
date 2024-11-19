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
@ApiModel(value = "异常断开次数-流量")
public class TrafficConnectionFlowVo {

    @ApiModelProperty(value = "异常断开次数",dataType = "int")
    private Integer disconnectTimes;
}
