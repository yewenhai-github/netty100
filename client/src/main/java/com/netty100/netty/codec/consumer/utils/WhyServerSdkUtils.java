package com.netty100.netty.codec.consumer.utils;

import com.netty100.netty.codec.consumer.constants.CodecErrorCode;
import com.netty100.netty.codec.consumer.service.WhyMessageConsumerService;
import com.netty100.common.exception.CommonException;
import com.netty100.common.utils.WhySpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public class WhyServerSdkUtils {

    public static WhyMessageConsumerService getTopeConsumerService(String url){
        Object bean;
        try {
            bean = WhySpringUtils.getBean(url);
        } catch (Exception e) {
            throw new CommonException(CodecErrorCode.Err007.getCodeMassage(e));
        }
        if (bean == null) {
            throw new CommonException(CodecErrorCode.Err008.getCodeMassage());
        }
        if (!(bean instanceof WhyMessageConsumerService)) {
            throw new CommonException(CodecErrorCode.Err009.getCodeMassage());
        }
        return (WhyMessageConsumerService) bean;
    }


}
