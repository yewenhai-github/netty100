package com.netty100.kernel.bus;

import com.google.common.eventbus.AsyncEventBus;
import com.netty100.kernel.bus.subscribe.ClientChannelActiveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventBusUtils {

    @Autowired
    private AsyncEventBus eventBus;

    public void post(ClientChannelActiveData event) {
        if (null != event) {
            eventBus.post(event);
        }
    }


}
