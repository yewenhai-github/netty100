package com.netty100.broker.devops.timer;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.entity.ChannelDto;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 停机上报（netty节点服务停机）
 * @author why
 * @version 1.0.0, 2022/6/9
 * @since 1.0.0, 2022/6/9
 */
@Slf4j
@Component
public class PushKernelNameServerExitRegister {
    @Autowired
    private WhyKernelProperties whyKernelProperties;

    public void doCommand(){
        if(!whyKernelProperties.pushKernelNameServerExitSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            return;
        }
        ChannelDto dto = new ChannelDto();
        dto.setIntranetIp(SysUtility.getHostIp());
        dto.setPort(String.valueOf(whyKernelProperties.getPort()));
        WhyCloudUtils.postBody(whyKernelProperties, RequestCode.Req06.getCode(), dto, 1);
    }
}
