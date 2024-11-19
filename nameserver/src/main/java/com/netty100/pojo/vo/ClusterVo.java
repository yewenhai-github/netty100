package com.netty100.pojo.vo;

import com.netty100.entity.Cluster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "集群信息")
public class ClusterVo extends Cluster {

    private static final long serialVersionUID = 4964465190016481412L;

    @ApiModelProperty(value = "游戏端连接总数")
    private Integer clientConnectionCountTotal;

    @ApiModelProperty(value = "服务器连接总数")
    private Integer serverConnectionCountTotal;

    @ApiModelProperty(value = "活跃节点数量")
    private Integer upServerCount;

    @ApiModelProperty(value = "总节点数量")
    private Integer totalServerCount;
}
