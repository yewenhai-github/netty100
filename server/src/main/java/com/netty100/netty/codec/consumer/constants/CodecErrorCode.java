package com.netty100.netty.codec.consumer.constants;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public enum CodecErrorCode {
    Err001("001","查找泛型对象异常"),
    Err002("002","收到客户端消息为空"),
    Err003("003","proto2报文解析异常"),
    Err004("004","json消息解析异常"),
    Err005("005","未知的报文结构"),
    Err006("006","消息解析失败 "),
    Err007("007","错误的接口 "),
    Err008("008","未知的接口 "),
    Err009("009","请检查是否实现接口TopeNettyStarterService"),
    Err010("010","data数据转换异常"),
    Err011("011","鉴权服务异常，基础服务网关地址调用失败"),
    Err012("012","鉴权服务异常，鉴权不通过"),
    Err013("013","鉴权结果解析异常"),
    Err014("014","鉴权结果异常，用户id为空"),
    Err099("099","未知错误"),

    ;

    private String code;
    private String massage;
    CodecErrorCode(String code, String massage) {
        this.code = code;
        this.massage = massage;
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

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getCodeMassage() {
        return "Err"+code+"->"+massage;
    }

    public String getCodeMassage(Exception e) {
        return "("+code+")"+massage+e+"\n";
    }
}