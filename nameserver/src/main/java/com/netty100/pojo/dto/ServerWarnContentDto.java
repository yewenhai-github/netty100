package com.netty100.pojo.dto;

import com.netty100.entity.Server;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/11
 */
@Data
@AllArgsConstructor
public class ServerWarnContentDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Server server;
    private String warnContent;
}
