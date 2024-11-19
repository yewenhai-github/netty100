package com.netty100.netty.server.annotation;

import com.netty100.netty.WhyNettyServerStart;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({WhyNettyServerStart.class})
public @interface EnableWhyNettyServer {

}
