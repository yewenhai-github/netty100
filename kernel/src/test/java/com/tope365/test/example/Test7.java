package com.netty100.test.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/12/28
 * @since 1.0.0, 2022/12/28
 */
public class Test7 {

    //根据Selector找到对应的SelectionKey
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8888));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey1 = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("A=" + selectionKey1 + " " + selectionKey1.hashCode());
        SelectionKey selectionKey2 = serverSocketChannel.keyFor(selector);
        System.out.println("B=" + selectionKey2 + " " + selectionKey2.hashCode());
        serverSocketChannel.close();
    }

}
