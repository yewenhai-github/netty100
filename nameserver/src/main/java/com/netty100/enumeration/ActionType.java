package com.netty100.enumeration;

/**
 * @author why
 */
public interface ActionType {

    int CLIENT_CONNECT = 1;

    int CLIENT_DISCONNECT = 2;

    int CLIENT_ERROR_DISCONNECT = 3;

    int CLIENT_HEARTBEAT_DISCONNECT = 4;

    int SERVER_CONNECT = 5;

    int SERVER_DISCONNECT = 6;

    int SERVER_ERROR_DISCONNECT = 7;

    int SERVER_HEARTBEAT_DISCONNECT = 8;
}
