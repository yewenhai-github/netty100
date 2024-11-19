package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
public class AccessLogPageQueryDto {

    private String ip;

    private String requestPath;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start = DateUtils.addDays(new Date(), -1);

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end = new Date();

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String orderBy = "create_time";

    private String orderFlag = "desc";
}
