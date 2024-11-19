package com.netty100.config.advice;

import com.netty100.utils.WebResult;
import com.netty100.utils.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 统一异常处理类
 *
 * @author why
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(CommonException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public WebResult<?> commonExceptionHandler(CommonException e) {
        log.error("全局异常捕获", e);
        return WebResult.error(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WebResult<?> bodyValidExceptionHandler(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String message = fieldErrors.get(0).getDefaultMessage();
        log.error("全局异常捕获:{}", message, exception);
        return WebResult.error(message);
    }

    @ExceptionHandler(value = java.lang.reflect.UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WebResult<?> flowException() {
        return WebResult.error("系统繁忙,请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public WebResult<?> exceptionHandler(Exception e) {
        log.error("全局异常捕获", e);
        return WebResult.error(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public WebResult<?> httpMessageNotReadableHandler(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableHandler", e);
        return WebResult.error("Required request body is missing");
    }
}
