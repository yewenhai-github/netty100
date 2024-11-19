package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "netty节点启动上报信息")
public class BootReportDto {

    /**
     * 内网地址
     */
    @NotBlank(message = "内网地址不能为空")
    @ApiModelProperty(value = "内网地址", required = true, dataType = "string")
    private String intranetIp;

    /**
     * 绑定端口
     */
    @NotBlank(message = "绑定端口不能为空")
    @ApiModelProperty(value = "绑定端口号", required = true, dataType = "string")
    private String port;

    /**
     * 启动时间
     */
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "启动时间,时间格式为yyyy-MM-dd HH:mm:ss", required = true, dataType = "string")
    private Date lastBootTime;
}
