package com.netty100.kernel.protocol.ssl;

import com.netty100.kernel.autoconfig.WhyKernelProperties;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.io.InputStream;

@Component
public class SslFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SslFactory.class);

    private static final String CERT_FILE_NAME = "why.tcp.crt";
    private static final String KEY_FILE_NAME = "why.tcp.key";

    @Resource
    private WhyKernelProperties connectConf;

    private SslContext sslContext;

    @PostConstruct
    private void initSslContext() {
        if (!connectConf.isEnableTlsSever()) {
            return;
        }
        try {
            InputStream certStream = new ClassPathResource(CERT_FILE_NAME).getInputStream();
            InputStream keyStream = new ClassPathResource(KEY_FILE_NAME).getInputStream();
            SslContextBuilder contextBuilder = SslContextBuilder.forServer(certStream, keyStream);
            contextBuilder.clientAuth(ClientAuth.OPTIONAL);
            contextBuilder.sslProvider(OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK);
            if (connectConf.isNeedClientAuth()) {
                LOG.info("client tls authentication is required.");
                contextBuilder.clientAuth(ClientAuth.REQUIRE);
                contextBuilder.trustManager(certStream);
            }
            sslContext = contextBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException("failed to initialize ssl context.", e);
        }
    }

    public SSLEngine buildSslEngine(SocketChannel ch) {
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(connectConf.isNeedClientAuth());
        return sslEngine;
    }

}

