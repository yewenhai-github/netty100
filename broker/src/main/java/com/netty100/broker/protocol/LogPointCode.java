package com.netty100.broker.protocol;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public enum LogPointCode {
    //消息日志
    M01("01","c2s-消息接收成功 "),
    M02("02","c2s-消息发送成功 "),
    M03("03","c2s-消息发送失败,服务器不在线 "),
    M04("04","c2s-消息接收错误 "),

    M31("31","s2c-消息读取成功 "),
    M32("32","s2c-消息发送成功 "),
    M33("33","s2c-消息发送失败,用户不在线 "),
    M34("34","s2c-消息接收错误 "),

    M41("41","s2c-消息转发(定点)发送成功 "),
    M42("42","s2c-消息转发(定点)发送命中失败，消息丢弃 "),
    M43("43","s2c-消息转发(定点)消费命中失败，消息丢弃 "),
    M44("44","s2c-消息转发(定点)消费成功 "),
    M45("45","s2c-消息转发(群发)发送成功 "),
    M46("46","s2c-消息转发(群发)消费命中失败，消息丢弃 "),
    M47("47","s2c-消息转发(群发)消费成功 "),
    M48("48","s2c-转发消费未知错误 "),

    M61("61","s2c-广播消费向其他内核节点通知成功 "),
    M62("62","s2c-广播消费成功 "),

    M99("99","未知错误 "),
    //内核日志
    K01("01","kernel server is crash "),
    K02("02","access cloud error,uri undefine "),
    K03("03","access cloud error,http post error "),
    K04("04","access cloud error,http post IOException, uri= "),

    //连接日志
    C01("01","client connect success"),
    C02("02","client disconnect-normal"),
    C03("03","client disconnect-error"),
    C04("04","client disconnect-idle"),
    C05("05","client disconnect-force 消息协议未配置"),
    C06("06","client disconnect-force 客户端连接未认证"),
    C07("07","client disconnect-force 客户端消息与配置不一致"),
    C08("08","client disconnect-force 客户端配置失败,消息协议客户端来源不能小于0"),
    C09("09","client disconnect-force 客户端配置失败,消息协议服务器来源不能小于0"),
    C10("10","client disconnect-force 客户端配置失败,消息协议未配置"),
    C11("11","client disconnect-force 客户端连接校验失败,注册码不能为空"),
    C12("12","client disconnect-force 客户端连接校验失败,注册码获取失败"),
    C13("13","client disconnect-force 客户端连接校验失败,注册码不一致"),
    C14("14","client disconnect-force 客户端连接校验失败,一体化平台访问失败"),
    C15("15","client disconnect-force 消息不合法，无法解析"),
    C16("16","client disconnect-force 用户已在其他pad登录"),
    C17("17","client disconnect-force 客户端配置不正确，消息与配置不一致"),
    C18("18","client disconnect-force 用户强制退出，用户重复登录"),


    C51("51","server web disconnect-normal"),
    C52("52","server job disconnect-normal"),
    C53("53","server mq disconnect-normal"),
    C54("54","server web disconnect-error"),
    C55("55","server job disconnect-error"),
    C56("56","server mq disconnect-error"),
    C57("57","server web disconnect-idle"),
    C58("58","server job disconnect-idle"),
    C59("59","server mq disconnect-idle"),
    C60("60","server simplex disconnect-normal"),
    C61("61","server simplex disconnect-error"),
    C62("61","server simplex disconnect-idle"),


    ;

    private String code;
    private String message;
    LogPointCode(String code, String message) {
        this.code = code;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public static String getMassageByCode(String code) {
        for (LogPointCode logPointCode : LogPointCode.values()) {
            if (logPointCode.getCode().equals(code)) {
                return logPointCode.getMessage();
            }
        }
        return null;
    }
}
