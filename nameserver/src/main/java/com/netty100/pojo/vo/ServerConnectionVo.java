package com.netty100.pojo.vo;

import com.netty100.entity.ServerConnection;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class ServerConnectionVo extends ServerConnection {

    private String messageWayDesc;

    private String messageSourceDesc;

    private String messageDestDesc;

    private String messageTypeDesc;

    private String messageSerializeDesc;
}
