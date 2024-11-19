package com.netty100.service;


import com.netty100.pojo.dto.OnlineDebugDto;

/**
 * @author why
 */
public interface OnlineDebugService {

    String onlineDebug(OnlineDebugDto dto);

    String connect(OnlineDebugDto dto);

    String disconnect(OnlineDebugDto dto);
}
