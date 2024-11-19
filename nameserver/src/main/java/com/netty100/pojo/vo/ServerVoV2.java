package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点描述")
public class ServerVoV2 {

    @ApiModelProperty(value = "外网域名")
    private String domain;

    @ApiModelProperty(value = "外网IP地址")
    private String ip;

    @ApiModelProperty(value = "端口号")
    private String port;

    public ServerVoV2(ServerVo vo) {
        this.domain = vo.getDomain();
        this.ip = vo.getExtranetIp();
        this.port = vo.getPort();
    }

}