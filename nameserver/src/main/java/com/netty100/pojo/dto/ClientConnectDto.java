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
@ApiModel(value = "游戏端新建连接")
public class ClientConnectDto {

    @NotBlank(message = "节点内网地址不能为空")
    @ApiModelProperty(value = "节点内网地址", dataType = "string", required = true)
    private String intranetIp;

    @NotBlank(message = "节点绑定端口号不能为空")
    @ApiModelProperty(value = "节点绑定端口号", dataType = "string", required = true)
    private String port;

    @Valid
    @ApiModelProperty(value = "属性信息", required = true)
    private List<Property> properties;

    @Getter
    @Setter
    @ApiModel(value = "游戏端连接属性")
    public static class Property {

        @NotBlank(message = "channel id 不能为空")
        @ApiModelProperty(value = "channel id", dataType = "string", required = true)
        private String channelId;

        @NotBlank(message = "连接唯一标识不能为空")
        @ApiModelProperty(value = "连接唯一标识", dataType = "string", required = true)
        private String channelKey;

        @ApiModelProperty(value = "设备标识", dataType = "long", required = true)
        private Long deviceId;

        @NotNull(message = "userId不能为空")
        @ApiModelProperty(value = "游戏端userId", dataType = "long", required = true)
        private Long userId;

        @ApiModelProperty(value = "连接地址", dataType = "string", required = true)
        private String remoteIp;

        @NotNull(message = "连接创建时间不能为空")
        @ApiModelProperty(value = "连接创建时间", dataType = "string", required = true)
        @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date connectTime;

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
    }
}
