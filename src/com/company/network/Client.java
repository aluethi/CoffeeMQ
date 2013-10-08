package com.company.network;

import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/8/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client implements Runnable {

    private final SocketChannel channel_;

    public Client(SocketChannel channel) {
        channel_ = channel;
    }

    @Override
    public void run() {

    }
}
