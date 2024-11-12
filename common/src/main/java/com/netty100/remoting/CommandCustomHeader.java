package com.netty100.remoting;

import com.netty100.remoting.exception.RemotingException;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public interface CommandCustomHeader {
    void checkFields() throws RemotingException.RemotingCommandException;
}
