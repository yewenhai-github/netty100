package com.netty100.pojo.vo;

import cn.hutool.core.date.BetweenFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.netty100.enumeration.ServerStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "节点列表返回对象")
public class ServerListVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务器主键")
    private Integer id;

    @ApiModelProperty(value = "集群id")
    private Integer clusterId;

    @ApiModelProperty(value = "外网IP地址")
    private String extranetIp;

    @ApiModelProperty(value = "内网IP地址")
    private String intranetIp;

    @ApiModelProperty(value = "端口号")
    private String port;

    @ApiModelProperty(value = "netty服务器状态")
    private Integer serverStatus;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上一次启动时间")
    private Date lastBootTime;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后一次心跳时间")
    private Date lastHeartBeatTime;

    @ApiModelProperty(value = "自节点添加以来的启动次数")
    private Integer bootTimes;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "运行时长")
    public String getRuntime() {
        if (Objects.isNull(this.lastBootTime) || Objects.equals(serverStatus, ServerStatus.DOWN)) {
            return null;
        }
        return new BetweenFormatter(System.currentTimeMillis() - this.lastBootTime.getTime(), BetweenFormatter.Level.SECOND, 5).format();
    }

    @ApiModelProperty(value = "游戏端连接数")
    private Integer clientConnectionCount;

    @ApiModelProperty(value = "服务器连接数")
    private Integer serverConnectionCount;
}
