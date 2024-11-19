package com.netty100.test.example;


import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

/**
 * @author why
 * @version 1.0.0, 2022/12/28
 * @since 1.0.0, 2022/12/28
 */
public class Test8 {

    public static void main(String[] args) throws IOException {

        SelectorProvider provider1 = SelectorProvider.provider();
        System.out.println(provider1);
        ServerSocketChannel serverSocketChannel = null;
        serverSocketChannel = serverSocketChannel.open();
        SelectorProvider provider2 = serverSocketChannel.provider();
        System.out.println(provider2);
        serverSocketChannel.close();

    }
}
