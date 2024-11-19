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
@ApiModel(value = "监控tps")
public class MonitorTps {

    @ApiModelProperty(value = "c2pTps", dataType = "int")
    private Integer c2pTps;

    @ApiModelProperty(value = "p2sTps", dataType = "int")
    private Integer p2sTps;

    @ApiModelProperty(value = "s2pTps", dataType = "int")
    private Integer s2pTps;

    @ApiModelProperty(value = "p2cTps", dataType = "int")
    private Integer p2cTps;

    @ApiModelProperty(value = "游戏端连接数",dataType = "int")
    private Integer clientConnectCount;

    @ApiModelProperty(value = "服务器连接数",dataType = "int")
    private Integer serverConnectCount;
}
