package com.netty100.pojo.vo;

import com.netty100.entity.WarnHistoryInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "告警页面结果vo")
public class MonitorResultVo {

    @ApiModelProperty(value = "游戏端连接")
    private ArrayList<ClientConnectVo> clientConnectVos;

    @ApiModelProperty(value = "服务器连接")
    private ArrayList<ServerConnectVo> serverConnectVos;

    @ApiModelProperty(value = "转发率相关数据")
    private ForwardStatus forwardStatus;

    @ApiModelProperty(value = "服务异常断开次数,总流量")
    private TrafficConnectionFlowVo trafficConnectionFlowVo;

    @ApiModelProperty(value = "集群存活节点数量")
    private int activeServerCount;

    @ApiModelProperty(value = "集群总节点数量")
    private int totalServerCount;

    @ApiModelProperty(value = "当日累计流量",dataType = "long")
    private BigInteger dayTotalTraffic;

    @ApiModelProperty(value = "服务器连接数,游戏端连接数,tps数据")
    private MonitorTps monitorTps;

    @ApiModelProperty(value = "tps峰值,qps峰值")
    private MonitorTpsQpsPeak monitorTpsQpsPeak;

    @ApiModelProperty(value = "新增告警列表")
    private List<WarnHistoryInfo> warnHistoryInfoList;

    @ApiModelProperty(value = "新增告警-告警等级")
    private Map<Integer, LongAdder> warnLevelDetails;

    @ApiModelProperty(value = "消息失败率")
    private MessageRateVo messageRateVo;
}
