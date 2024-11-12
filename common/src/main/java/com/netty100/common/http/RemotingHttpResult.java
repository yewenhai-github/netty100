package com.netty100.common.http;

import lombok.Data;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/7
 * @since 1.0.0, 2022/4/7
 */
@Data
public class RemotingHttpResult {
    private int responseCode;
    private String responseStr;
}
