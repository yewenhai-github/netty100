package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value = "netty节点", description = "")
public class Server implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务器主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "集群id", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "外网域名", dataType = "string")
    private String domain;

    @ApiModelProperty(value = "外网IP地址", dataType = "string")
    private String extranetIp;

    @ApiModelProperty(value = "内网IP地址", dataType = "string")
    private String intranetIp;

    @ApiModelProperty(value = "端口号", dataType = "string")
    private String port;

    @ApiModelProperty(value = "netty服务器状态", dataType = "int")
    private Integer serverStatus;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上一次启动时间", dataType = "string")
    private Date lastBootTime;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后一次心跳时间", dataType = "string")
    private Date lastHeartBeatTime;

    @ApiModelProperty(value = "自节点添加以来的启动次数", dataType = "int")
    private Integer bootTimes;

    @JsonIgnore
    @ApiModelProperty(value = "游戏端连接数", hidden = true)
    private Integer clientConnectCount;

    @JsonIgnore
    @ApiModelProperty(value = "服务器端连接数", hidden = true)
    private Integer serverConnectCount;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
