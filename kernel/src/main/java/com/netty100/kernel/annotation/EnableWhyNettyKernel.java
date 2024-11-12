package com.netty100.kernel.annotation;

import com.netty100.kernel.autoconfig.WhyKernelImport;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({WhyKernelImport.class})
public @interface EnableWhyNettyKernel {

}
