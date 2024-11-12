package com.netty100.remoting.exception;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public class RemotingConnectException extends RemotingException {
    private static final long serialVersionUID = -5565366231695911316L;

    public RemotingConnectException(String addr) {
        this(addr, null);
    }

    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to " + addr + " failed", cause);
    }

    public static class RemotingException extends Exception {
        private static final long serialVersionUID = -5690687334570505110L;

        public RemotingException(String message) {
            super(message);
        }

        public RemotingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
