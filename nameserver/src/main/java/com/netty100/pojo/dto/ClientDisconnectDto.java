package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "游戏端连接断开参数相关")
public class ClientDisconnectDto {

    @NotBlank(message = "节点内网地址不能为空")
    @ApiModelProperty(value = "节点内网地址", required = true, dataType = "string")
    private String intranetIp;

    @NotBlank(message = "节点绑定端口号不能为空")
    @ApiModelProperty(value = "节点绑定端口号", required = true, dataType = "string")
    private String port;

    @Valid
    @ApiModelProperty(value = "属性信息", required = true)
    private List<Property> properties;

    @Getter
    @Setter
    @ApiModel(value = "游戏端连接断开相关信息")
    public static class Property {

        @NotNull(message = "channel id不能为空")
        @ApiModelProperty(value = "channel Id", required = true, dataType = "string")
        private String channelId;

        @NotBlank(message = "连接唯一标识不能为空")
        @ApiModelProperty(value = "连接唯一标识", dataType = "string", required = true)
        private String channelKey;

        @NotNull(message = "连接断开时间不能为空")
        @ApiModelProperty(value = "连接断开时间", required = true, dataType = "string")
        @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date disConnectTime;

        @NotNull(message = "userId不能为空")
        @ApiModelProperty(value = "游戏端userId", dataType = "long", required = true)
        private Long userId;

        @ApiModelProperty(value = "设备标识", dataType = "long")
        private Long deviceId;

        @ApiModelProperty(value = "连接地址", dataType = "string", required = true)
        private String remoteIp;

    }
}
