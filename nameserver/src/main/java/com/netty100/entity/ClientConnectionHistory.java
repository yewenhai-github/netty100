package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "游戏端连接历史记录")
public class ClientConnectionHistory {

    @ApiModelProperty(value = "主键", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "节点主键", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "集群主键", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "节点内网IP",dataType = "string")
    private String serverIntranetIp;

    @ApiModelProperty(value = "channel id", dataType = "string")
    private String channelId;

    @ApiModelProperty(value = "channel唯一标识", dataType = "string")
    private String channelKey;

    @ApiModelProperty(value = "设备标识", dataType = "long")
    private Long deviceId;

    @ApiModelProperty(value = "游戏端用户主键", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "远程连接地址", dataType = "string")
    private String remoteIp;

    @ApiModelProperty(value = "动作类型", dataType = "int")
    private Integer actionType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发生时间", dataType = "string")
    private Date occurTime;
}
