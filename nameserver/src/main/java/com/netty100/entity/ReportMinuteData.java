package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Getter
@Setter
@ApiModel(value = "一分钟统计分析结果", description = "")
public class ReportMinuteData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "节点主键")
    private Integer serverId;

    @ApiModelProperty(value = "集群外键")
    private Integer clusterId;

    @ApiModelProperty(value = "游戏端连接数")
    private Integer clientConnectCount;

    @ApiModelProperty(value = "服务器连接数")
    private Integer serverConnectCount;

    @ApiModelProperty(value = "转发次数")
    private Integer forwardTimes;

    @ApiModelProperty(value = "消息转发率")
    private BigDecimal forwardRate;

    @ApiModelProperty(value = "消息投递失败率")
    private BigDecimal failedRate;

    @ApiModelProperty(value = "游戏端错误重连次数")
    private Integer clientErrorReconnectTimes;

    @ApiModelProperty(value = "游戏端心跳重连次数")
    private Integer clientIdleReconnectTimes;

    @ApiModelProperty(value = "服务器错误重连次数")
    private Integer serverErrorReconnectTimes;

    @ApiModelProperty(value = "服务器心跳重连次数")
    private Integer serverIdleReconnectTimes;

    @ApiModelProperty(value = "c2pTps")
    private Integer c2pTps;

    @ApiModelProperty(value = "c2pQps")
    private Integer c2pQps;

    @ApiModelProperty(value = "p2sTps")
    private Integer p2sTps;

    @ApiModelProperty(value = "p2sQps")
    private Integer p2sQps;

    @ApiModelProperty(value = "s2pTps")
    private Integer s2pTps;

    @ApiModelProperty(value = "s2pQps")
    private Integer s2pQps;

    @ApiModelProperty(value = "p2cTps")
    private Integer p2cTps;

    @ApiModelProperty(value = "p2cQps")
    private Integer p2cQps;

    @ApiModelProperty(value = "创建日期")
    private String createDate;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
