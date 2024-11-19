package com.netty100.broker.annotation;

import java.lang.annotation.*;

/**
 * annotation for job handler
 * @author 2022-5-25 21:06:49
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableMessageWay {

    byte value() default -127;

    int executor() default -1;
}
