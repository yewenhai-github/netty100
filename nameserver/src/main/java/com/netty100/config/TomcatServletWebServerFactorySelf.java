package com.netty100.config;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * @author why
 */
@Component
public class TomcatServletWebServerFactorySelf extends TomcatServletWebServerFactory {

    @Override
    protected void postProcessContext(Context context) {
        context.setManager(new NoSessionManager());
    }
}
