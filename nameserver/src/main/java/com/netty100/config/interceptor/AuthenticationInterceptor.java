package com.netty100.config.interceptor;

import com.netty100.config.AuthTokenConfig;
import com.netty100.utils.Jwts;
import com.netty100.utils.SecurityContext;
import com.netty100.utils.WebResult;
import com.netty100.utils.respons.ResponseMsg;
import com.netty100.utils.respons.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


/**
 * @author why
 */
@Slf4j
@Component
@EnableConfigurationProperties(value = AuthTokenConfig.class)
@SuppressWarnings("all")
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private AuthTokenConfig config;

    private String nodeToken;

    /**
     * App端,服务器,netty节点默认访问token
     */
    private List<String> defaultTokens;

    /**
     * App端,服务器默认访问token
     */
    private List<String> clientTokens;

    private Jwts jwts;

    @Autowired
    public void setJwts(Jwts jwts) {
        this.jwts = jwts;
    }

    @Autowired
    public void setConfig(AuthTokenConfig config) {
        this.config = config;
        nodeToken = config.getNodeToken();
        defaultTokens = Arrays.asList(config.getClientToken(), config.getNodeToken(), config.getServerToken());
        clientTokens = Arrays.asList(config.getClientToken(), config.getServerToken());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        val token = request.getHeader(Jwts.HEADER);
        if (!StringUtils.hasText(token)) {
            ResponseUtil.write(response, WebResult.error(ResponseMsg.NOT_LOGGED_IN));
            return false;
        }

        final String requestPath = request.getServletPath();
        //如果token为客户端token且访问路径不以/api/nameserver开头,则直接终止访问
        if (clientTokens.contains(token)) {
            if (requestPath.startsWith("/api/nameserver")) {
                return true;
            } else {
                ResponseUtil.write(response, WebResult.error(ResponseMsg.LIMITED_TOKEN));
                return false;
            }
        }
        if (nodeToken.equals(token)) {
            if (requestPath.startsWith("/api")) {
                return true;
            } else {
                ResponseUtil.write(response, WebResult.error(ResponseMsg.LIMITED_TOKEN));
                return false;
            }
        }
        if (jwts.verify(token)) {
            val user = jwts.getUser(token);
            SecurityContext.setUser(user);
            return true;
        } else {
            ResponseUtil.write(response, WebResult.error(ResponseMsg.INVALID_TOKEN));
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContext.clear();
    }
}
