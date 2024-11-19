package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "消息对象")
public class Message {

    @ApiModelProperty(value = "主键", hidden = true, dataType = "long")
    private Long id;

    @ApiModelProperty(value = "节点外键", hidden = true, dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "集群外键", hidden = true, dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "消息标识", dataType = "long")
    private Long messageId;

    @ApiModelProperty(value = "游戏端用户主键", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "设备标识", dataType = "long")
    private Long deviceId;

    @ApiModelProperty(value = "消息方式", dataType = "byte")
    private Byte messageWay;

    private String messageWayDesc;

    @ApiModelProperty(value = "消息源", dataType = "byte")
    private Byte messageSource;

    private String messageSourceDesc;

    @ApiModelProperty(value = "消息目的地", dataType = "byte")
    private Byte messageDest;

    private String messageDestDesc;

    @ApiModelProperty(value = "消息类型", dataType = "byte")
    private Byte messageType;

    private String messageTypeDesc;

    @ApiModelProperty(value = "序列化方式", dataType = "byte")
    private Byte messageSerialize;

    private String messageSerializeDesc;

    @ApiModelProperty(value = "消息点", dataType = "string")
    private String logPoint;

    @ApiModelProperty(value = "消息内容", dataType = "string")
    private String logContent;

    @ApiModelProperty(value = "netty节点本地地址", dataType = "string")
    private String localAddress;

    @ApiModelProperty(value = "连接地址", hidden = true)
    private String localAdddress;

    @ApiModelProperty(value = "netty节点本地端口号", dataType = "string")
    private String localPort;

    @ApiModelProperty(value = "远程连接地址", dataType = "string")
    private String remoteAddress;

    @ApiModelProperty(value = "远程端口号", dataType = "string")
    private String remotePort;

    @ApiModelProperty(value = "消息发送时间")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date logTime;

    @ApiModelProperty(value = "是否主节点")
    private Byte leaf;

    @ApiModelProperty(value = "记录创建时间", hidden = true)
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private List<Message> leafMessage;
}
