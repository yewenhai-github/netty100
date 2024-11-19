package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class FlowVo {

    private Long c2pFlow;

    private Long p2cFlow;

    private Long p2sFlow;

    private Long s2pFlow;

    private Long relayFlow;

    @ApiModelProperty(value = "创建时间 HH:MM")
    private String createTime;
}
