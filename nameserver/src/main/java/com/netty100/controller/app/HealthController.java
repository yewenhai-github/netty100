package com.netty100.controller.app;

import com.netty100.utils.WebResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author why
 */
@RestController
public class HealthController {

    @GetMapping(value = "/health")
    public WebResult<?> health() {
        return WebResult.ok();
    }
}
