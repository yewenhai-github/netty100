package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "节点描述")
public class ServerVo implements Serializable {

    private static final long serialVersionUID = -5041982621931130434L;

    @ApiModelProperty(value = "服务器主键")
    private Integer id;

    @ApiModelProperty(value = "集群id")
    private Integer clusterId;

    @ApiModelProperty(value = "外网域名")
    private String domain;

    @ApiModelProperty(value = "外网IP地址")
    private String extranetIp;

    @ApiModelProperty(value = "内网IP地址")
    private String intranetIp;

    @ApiModelProperty(value = "端口号")
    private String port;

    @ApiModelProperty(value = "netty服务器状态")
    private Integer serverStatus;

    @ApiModelProperty(value = "上一次启动时间")
    private Date lastBootTime;

    @ApiModelProperty(value = "最后一次心跳时间")
    private Date lastHeartBeatTime;

    @ApiModelProperty(value = "服务器连接数")
    private Integer serverConnectCount = 0;

    @ApiModelProperty(value = "游戏端连接数")
    private Integer clientConnectCount = 0;

    @ApiModelProperty(value = "自节点添加以来的启动次数")
    private Integer bootTimes = 0;

    @ApiModelProperty(value = "总连接数,App端+服务器")
    private Integer connectionCount = 0;

    @ApiModelProperty(value = "总连接数,App端+服务器")
    public Integer getConnectionCount() {
        return this.getClientConnectCount() + this.getServerConnectCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerVo serverVo = (ServerVo) o;
        return id.equals(serverVo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
