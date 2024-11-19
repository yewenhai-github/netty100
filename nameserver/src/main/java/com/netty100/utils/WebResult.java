package com.netty100.utils;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.springframework.util.StringUtils;


/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "响应结果")
public class WebResult<T> {

    @ApiModelProperty(value = "响应数据")
    private T data;

    @ApiModelProperty(value = "响应描述")
    private String message;

    @ApiModelProperty(value = "响应时间")
    private long timestamp;

    public static final String SUCCESS_MESSAGE = "请求成功";

    public static final String ERROR_MESSAGE = "请求失败,请稍后重试";

    private static long now() {
        return DateUtil.current();
    }

    public static WebResult<?> ok() {
        val result = new WebResult<>();
        result.setMessage(SUCCESS_MESSAGE);
        result.setTimestamp(now());
        return result;
    }

    public static <T> WebResult<T> ok(@NonNull T obj) {
        val result = new WebResult<T>();
        result.setData(obj);
        result.setMessage(SUCCESS_MESSAGE);
        result.setTimestamp(now());
        return result;
    }

    public static WebResult<?> error(String message) {
        if (!StringUtils.hasText(message)) {
            return error();
        }
        val result = new WebResult<>();
        result.setMessage(message.trim());
        result.setTimestamp(now());
        return result;
    }

    public static WebResult<?> error() {
        val result = new WebResult<>();
        result.setMessage(ERROR_MESSAGE);
        result.setTimestamp(now());
        return result;
    }
}