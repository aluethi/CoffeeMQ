package com.company.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/14/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Acceptor implements Runnable {

    private static Logger LOGGER_ = Logger.getLogger(Acceptor.class.getCanonicalName());

    private Selector selector_;
    private final String host_;
    private final int port_;
    private ServerSocketChannel serverChannel_;
    private boolean isRunning_;

    public Acceptor(String host, int port) {
        host_ = host;
        port_ = port;
        isRunning_ = true;
        init();
    }

    public void init() {
        LOGGER_.log(Level.INFO, "Initializing Acceptor");
        try {
            selector_ = Selector.open();
            serverChannel_ = ServerSocketChannel.open();
            serverChannel_.configureBlocking(false);
            serverChannel_.socket().bind(new InetSocketAddress(host_, port_));
            SelectionKey key = serverChannel_.register(selector_, SelectionKey.OP_ACCEPT);
            key.attach(new AcceptorHandler(serverChannel_, selector_));
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open the selector or server socket channel");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(isRunning_) {
            try {
                selector_.select();
                Set<SelectionKey> selected = selector_.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    dispatch(key);
                }
                selected.clear();
            } catch (IOException e) {
                LOGGER_.log(Level.SEVERE, "Error while selecting SelectionKey");
                throw new RuntimeException(e);
            }
        }
    }

    void dispatch(SelectionKey key) {
        LOGGER_.log(Level.INFO, "Dispatching handler");
        Handler h = (Handler) key.attachment();
        if(h != null) {
            h.run();
        }
    }
}
