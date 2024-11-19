package com.netty100.remoting.exception;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public class RemotingException extends Exception {
    private static final long serialVersionUID = -5690687334570505110L;

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class RemotingCommandException extends RemotingConnectException.RemotingException {
        private static final long serialVersionUID = -6061365915274953096L;

        public RemotingCommandException(String message) {
            super(message, null);
        }

        public RemotingCommandException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
