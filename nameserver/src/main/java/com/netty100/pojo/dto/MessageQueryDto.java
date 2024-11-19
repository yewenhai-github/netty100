package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "消息分页查询")
public class MessageQueryDto {

    @ApiModelProperty(value = "节点外键", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "集群外键", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "消息标识", dataType = "long")
    private Long messageId;

    @ApiModelProperty(value = "游戏端用户主键", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "日志点",dataType = "string")
    private String logPoint;

    @ApiModelProperty(value = "设备标识", dataType = "long")
    private Long deviceId;

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
    private String remoteAddress;

    @ApiModelProperty(value = "查询开始时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start = DateUtils.addDays(new Date(), -1);

    @ApiModelProperty(value = "查询结束时间", dataType = "string")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end = new Date();

    @ApiModelProperty(value = "第几页,默认为1", dataType = "int")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空")
    private String orderBy = "log_time";

    @ApiModelProperty(value = "排序方式,默认为降序")
    private String orderFlag = "desc";

    @ApiModelProperty(value = "偏移量", hidden = true)
    public int getOffsetIndex() {
        return (pageNum - 1) * pageSize;
    }
}
