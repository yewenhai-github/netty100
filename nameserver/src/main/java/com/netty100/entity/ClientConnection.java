package com.netty100.entity;

import cn.hutool.core.date.BetweenFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/**
 * @author why
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "游戏端连接对象")
public class ClientConnection {

    @ApiModelProperty(value = "游戏端连接主键", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "节点外键", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "集群外键", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "节点内网IP", dataType = "string")
    private String serverIntranetIp;

    @ApiModelProperty(value = "channel id", dataType = "string")
    private String channelId;

    @ApiModelProperty(value = "channel标识", dataType = "string")
    private String channelKey;

    @ApiModelProperty(value = "设备标识", dataType = "long")
    private Long deviceId;

    @ApiModelProperty(value = "游戏端用户主键", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "消息方式", dataType = "byte")
    private Byte messageWay;

    @ApiModelProperty(value = "消息源", dataType = "byte")
    private Byte messageSource;

    @ApiModelProperty(value = "消息目的地", dataType = "byte")
    private Byte messageDest;

    @ApiModelProperty(value = "消息类型", dataType = "byte")
    private Byte messageType;

    @ApiModelProperty(value = "序列化方式", dataType = "byte")
    private Byte messageSerialize;

    @ApiModelProperty(value = "远程连接地址", dataType = "string")
    private String remoteIp;

    @ApiModelProperty(value = "连接时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date connectTime;

    @ApiModelProperty(value = "上一次心跳时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastHeartbeatTime;

    @ApiModelProperty(value = "连接时长", dataType = "string")
    public String getRuntime() {
        if (Objects.isNull(connectTime)) {
            return null;
        }
        return new BetweenFormatter(System.currentTimeMillis() - this.connectTime.getTime(), BetweenFormatter.Level.SECOND, 5).format();
    }
}
