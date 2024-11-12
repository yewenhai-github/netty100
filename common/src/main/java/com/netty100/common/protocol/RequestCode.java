package com.netty100.common.protocol;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public enum RequestCode {
    Req01("01","获取消息协议全局配置"),
    Req02("02","获取集群信息"),
    Req03("03","获取内核连接地址"),
    Req04("04","netty节点基本信息启动上报"),
    Req05("05","netty节点基本信息注册上报"),
    Req06("06","停机上报（netty节点服务停机）"),
    Req07("07","统计量上报（游戏端、内核、服务器）"),
    Req11("11","连接信息上报（游戏端连接）"),
    Req12("12","连接信息上报（游戏端正常断开）"),
    Req13("13","连接信息上报（游戏端异常断开）"),
    Req14("14","连接信息上报（游戏端心跳断开）"),
    Req15("15","连接信息上报（服务器连接）"),
    Req16("16","连接信息上报（服务器正常断开）"),
    Req17("17","连接信息上报（服务器异常断开）"),
    Req18("18","连接日志上报"),
    Req19("19","消息日志上报"),
    Req20("20","内核日志上报"),
    Req21("21","客户端心跳报文上报"),
    Req22("22","服务器心跳报文上报"),
    Req23("23","获取设备注册码"),
    Req24("24","获取消息协议")

    ;

    private String code;
    private String massage;
    RequestCode(String code, String massage) {
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
        for (RequestCode requestCode : RequestCode.values()) {
            if (requestCode.getCode().equals(code)) {
                return requestCode.getMassage();
            }
        }
        return null;
    }
}
