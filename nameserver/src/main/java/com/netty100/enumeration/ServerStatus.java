package com.netty100.enumeration;

/**
 * netty节点状态
 * @author why
 */
public interface ServerStatus {

    /**
     * 正在运行
     */
    int UP = 1;

    /**
     * 未知原因宕机
     */
    int DOWN = 2;
}
