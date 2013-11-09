package com.company.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
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
    private ServerSocketChannel serverChannel_;
    private final ExecutorService executor_;
    private boolean isRunning_;

    public Acceptor(String host, int port, ExecutorService executor) {
        executor_ = executor;
        isRunning_ = true;
        init(host, port);
    }

    /**
     * Initializing the selector, opening the server socket and binding it to the desired network interface and port.
     *
     * @param host Desired network interface
     * @param port Desired port
     */
    private void init(String host, int port) {
        LOGGER_.log(Level.INFO, "Initializing Acceptor on " + host + ":" + port);
        try {
            selector_ = Selector.open();
            serverChannel_ = ServerSocketChannel.open();
            serverChannel_.configureBlocking(false);
            serverChannel_.socket().bind(new InetSocketAddress(host, port));
            SelectionKey key = serverChannel_.register(selector_, SelectionKey.OP_ACCEPT);

            // Attaching the AcceptorHandler to the server channel
            key.attach(new AcceptorHandler(serverChannel_, selector_, executor_));
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open the selector or server socket channel");
            throw new RuntimeException(e);
        }
    }

    /**
     * Selecting channels on the selector.
     */
    @Override
    public void run() {
        while(isRunning_) {
            try {
                selector_.select();
                Set<SelectionKey> selected = selector_.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    dispatch(key);
                }
            } catch (IOException e) {
                LOGGER_.log(Level.SEVERE, "Error while selecting SelectionKey");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Dispatching an attached handler of a selection key.
     *
     * @param key
     */
    void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        if(r != null) {
            LOGGER_.log(Level.INFO, "Dispatching handler");
            r.run();
        }
    }
}
