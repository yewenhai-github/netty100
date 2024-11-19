package com.netty100.common.protocol;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public enum ResponseCode {
    Rep101("101","客户端连接成功"),
    Rep102("102","客户端心跳确认"),
    Rep103("103","客户端消息处理失败"),
    Rep104("104","客户端连接正常断开"),
    Rep105("105","客户端连接异常断开"),
    Rep106("106","客户端连接心跳断开"),
    Rep107("107","客户端连接未认证"),
    Rep108("108","客户端认证失败，设备密码不能为空"),
    Rep109("109","客户端认证失败，设备未注册"),
    Rep110("110","客户端认证失败，设备密码错误"),
    Rep111("111","客户端认证失败，静态服务器访问失败"),
    Rep112("112","客户端配置失败，消息协议客户端来源不能小于0"),
    Rep113("113","客户端配置失败，消息协议服务器来源不能小于0"),
    Rep114("114","客户端配置失败，消息协议未配置"),
    Rep115("115","服务器不在线"),
    Rep116("116","用户强制退出，用户已在其他pad登录"),
    Rep117("117","客户端配置不正确，消息与配置不一致"),
    Rep118("118","用户强制退出，用户重复登录"),

    Rep201("201","服务器连接成功"),
    Rep202("202","服务器心跳确认"),
    Rep203("203","服务器消息处理失败"),
    Rep204("204","服务器连接正常断开"),
    Rep205("205","服务器连接异常断开"),

    Rep300("300","server doCommand error"),

    Rep900("900","未知错误"),
    Rep901("901","消息不合法，无法解析"),
    Rep902("902","消息不能为空"),
    Rep903("903","此消息方式未实现"),

    ;

    private String code;
    private String massage;
    ResponseCode(String code, String massage) {
        this.code = code;
        this.massage = massage;
    }

    public byte[] getCodeBytes() {
        try {
            return code.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("getCodeBytes转换失败{}-{}", code, e);
            return code.getBytes();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMassage() {
        return massage;
    }

    public static String getMassageByCode(String code) {
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode.getMassage();
            }
        }
        return null;
    }
}
