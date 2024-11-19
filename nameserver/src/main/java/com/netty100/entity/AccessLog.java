package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
public class AccessLog {

    private Long id;

    private String ip;

    private String platformName;

    private String requestPath;

    private int costMills;

    private int hasError;

    private String createDate;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
