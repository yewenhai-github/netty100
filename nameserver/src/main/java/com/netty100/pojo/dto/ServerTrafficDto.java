package com.netty100.pojo.dto;

import com.netty100.entity.ServerTraffic;
import lombok.Data;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/30
 */
@Data
public class ServerTrafficDto extends ServerTraffic {
    private Integer id;
    private Long totalTraffic;
    private Long totalCount;
}
