package com.netty100.config.interceptor;


import cn.hutool.core.date.DateUtil;
import com.netty100.entity.AccessLog;
import com.netty100.service.AccessLogService;
import com.netty100.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author why
 */
@Slf4j
@Component
public class AccessLogInterceptor extends HandlerInterceptorAdapter {

    private final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    private AccessLogService accessLogService;

    private IpUtils ipUtils;

    @Autowired
    public void setIpUtils(IpUtils ipUtils) {
        this.ipUtils = ipUtils;
    }

    @Autowired
    public void setAccessLogService(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        threadLocal.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long start = threadLocal.get();
        threadLocal.remove();
        AccessLog accessLog = new AccessLog();
        accessLog.setCostMills((int) (System.currentTimeMillis() - start));
        accessLog.setIp(ipUtils.getRemoteIpByServletRequest(request));
        accessLog.setCreateDate(DateUtil.today());
        accessLog.setPlatformName("netty-cloud");
        accessLog.setRequestPath(request.getServletPath());
        if (Objects.isNull(ex)) {
            accessLog.setHasError(0);
        } else {
            accessLog.setHasError(1);
        }
        try {
            accessLogService.save(accessLog);
            log.info("ip:{},访问路径:{},耗时(毫秒):{}", accessLog.getIp(), accessLog.getRequestPath(), accessLog.getCostMills());
        } catch (Exception e) {
            log.error("访问日志存储失败", e);
        }
    }

}
