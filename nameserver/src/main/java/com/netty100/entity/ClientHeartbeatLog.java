package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "游戏端心跳信息")
public class ClientHeartbeatLog {

    @ApiModelProperty(value = "主键", hidden = true)
    private Long id;

    @ApiModelProperty(value = "channel标识")
    private String channelId;

    @ApiModelProperty(value = "channel key")
    private String channelKey;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "心跳时间")
    private Date idleTime;

    @ApiModelProperty(value = "设备标识")
    private Long deviceId;

    @ApiModelProperty(value = "用户标识")
    private Long userId;

    @ApiModelProperty(value = "netty节点本地地址")
    private String localAddress;

    @ApiModelProperty(value = "netty节点本地绑定端口号")
    private String localPort;

    @ApiModelProperty(value = "远程连接地址")
    private String remoteAddress;

    @ApiModelProperty(value = "远程端口号")
    private String remotePort;

    @ApiModelProperty(value = "消息方式")
    private Byte messageWay;

    @ApiModelProperty(value = "消息源")
    private Byte messageSource;

    @ApiModelProperty(value = "消息方式")
    private Byte messageType;

    @ApiModelProperty(value = "消息目的地")
    private Byte messageDest;

    @ApiModelProperty(value = "消息序列化方式")
    private Byte messageSerialize;
}
