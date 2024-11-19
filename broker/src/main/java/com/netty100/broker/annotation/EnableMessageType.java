package com.netty100.broker.annotation;

import java.lang.annotation.*;

/**
 * annotation for job handler
 * @author 2023-6-25 21:06:49
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableMessageType {

    byte[] value();

}
