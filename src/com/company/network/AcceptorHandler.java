package com.company.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/14/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AcceptorHandler extends Handler {

    private static Logger LOGGER_ = Logger.getLogger(AcceptorHandler.class.getCanonicalName());

    private final ServerSocketChannel serverChannel_;
    private final Selector selector_;
    private final ExecutorService executor_;

    public AcceptorHandler(ServerSocketChannel serverChannel, Selector selector, ExecutorService executor) {
        serverChannel_ = serverChannel;
        selector_ = selector;
        executor_ = executor;
    }

    @Override
    public void run() {
        try {
            SocketChannel channel = serverChannel_.accept();
            if(channel != null) {
                LOGGER_.log(Level.INFO, "Accepted new connection from: " + channel.socket().getRemoteSocketAddress());
                channel.configureBlocking(false);
                selector_.wakeup();
                SelectionKey key = channel.register(selector_, SelectionKey.OP_READ);
                key.attach(new ClientHandler(key, channel, executor_));
            }
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open client socket channel");
            throw new RuntimeException(e);
        }
    }
}
