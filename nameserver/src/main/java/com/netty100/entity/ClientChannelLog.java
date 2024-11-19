package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 客户端连接日志
 *
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "连接日志")
public class ClientChannelLog {

    @ApiModelProperty(value = "主键", dataType = "long", hidden = true)
    private Long id;

    @ApiModelProperty(value = "节点主键", dataType = "int",hidden = true)
    private Integer serverId;

    @ApiModelProperty(value = "集群主键", dataType = "int",hidden = true)
    private Integer clusterId;

    @ApiModelProperty(value = "集群名称", dataType = "string",hidden = true)
    private String clusterName;

    @ApiModelProperty(value = "日志点", dataType = "string")
    private String logPoint;

    @ApiModelProperty(value = "日志内容", dataType = "string")
    private String logContent;

    @ApiModelProperty(value = "日志时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logTime;

    @ApiModelProperty(value = "netty节点本地地址", dataType = "string")
    private String localAddress;

    @ApiModelProperty(value = "netty节点本地绑定端口号", dataType = "string")
    private String localPort;

    @ApiModelProperty(value = "远程连接地址", dataType = "string")
    private String remoteAddress;

    @ApiModelProperty(value = "远程端口号", dataType = "string")
    private String remotePort;

    @ApiModelProperty(value = "用户主键", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "消息源", dataType = "byte")
    private Byte messageSource;

    @ApiModelProperty(value = "消息目的地", dataType = "byte")
    private Byte messageDest;

}
