package com.company.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/1/13
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Acceptor implements Runnable {

    private static Logger LOGGER_ = Logger.getLogger(Acceptor.class.getCanonicalName());

    private String inetAddress_;
    private int port_;
    private Executor executor_;
    private volatile boolean isRunning_;
    private ServerSocketChannel serverChannel_;

    public Acceptor(String inetAddress, int port, Executor executor) {
        inetAddress_ = inetAddress;
        port_ = port;
        executor_ = executor;
        init();
    }

    private void init() {
        try {
            serverChannel_ = ServerSocketChannel.open();
            serverChannel_.configureBlocking(true);
            serverChannel_.socket().bind(new InetSocketAddress(inetAddress_, port_));
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open the server socket channel.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(isRunning_) {
            try {
                SocketChannel channel = serverChannel_.accept();
                Client c = new Client(channel);
                executor_.execute(c);
            } catch (IOException e) {
                LOGGER_.log(Level.WARNING, "Could not accept connection from client.");
            }
        }
    }

    public void shutdown() {
        if(isRunning_) {
            isRunning_ = false;
        }
    }

    String getInetAddress() {
        return inetAddress_;
    }

    int getPort() {
        return port_;
    }

}
