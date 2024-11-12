package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class ChannelDto implements Serializable {
    private static final long serialVersionUID = 1L;

    String intranetIp;
    String port;
    Object[] properties;
}
