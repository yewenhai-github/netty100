package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "节点简略信息")
public class ServerBriefVo {

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
}
