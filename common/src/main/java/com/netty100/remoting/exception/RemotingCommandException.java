package com.netty100.remoting.exception;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public class RemotingCommandException extends RemotingException {
    private static final long serialVersionUID = -6061365915274953096L;

    public RemotingCommandException(String message) {
        super(message, null);
    }

    public RemotingCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
