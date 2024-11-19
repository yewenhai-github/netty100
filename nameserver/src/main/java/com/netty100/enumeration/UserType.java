package com.netty100.enumeration;

/**
 * 用户类型,只有管理员才可以设置接收告警状态
 * @author why
 */
public interface UserType {

    /**
     * 普通用户
     */
    int USER = 0;

    /**
     * 管理员
     */
    int ADMIN = 1;
}
