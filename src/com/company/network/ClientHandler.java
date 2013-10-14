package com.company.network;

import com.company.ExecutionEngine;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/14/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler extends Handler {

    private static Logger LOGGER_ = Logger.getLogger(ClientHandler.class.getCanonicalName());

    private final SocketChannel channel_;
    private final SelectionKey key_;
    private ByteBuffer buffer_;
    private ExecutionEngine engine_;

    public ClientHandler(SelectionKey key, SocketChannel channel) {
        LOGGER_.log(Level.INFO, "Instantiating ClientHandler");
        key_ = key;
        channel_ = channel;
        buffer_ = ByteBuffer.allocate(2048);
        engine_ = new ExecutionEngine();
    }

    @Override
    public void run() {
        if(key_.isReadable()) {
            read();
        } else if(key_.isWritable()) {
            write();
        }
    }

    public void read() {
        LOGGER_.log(Level.INFO, "Reading from the network");
        try {
            buffer_.clear();
            int readCount = channel_.read(buffer_);
            if(readCount > 0) {
                // process changes buffer_ content
                engine_.process(buffer_);
                key_.interestOps(SelectionKey.OP_WRITE);
            } else {
                channel_.close();
                key_.cancel();
            }
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not read from socket channel");
            throw new RuntimeException(e);
        }

    }

    public void write() {
        LOGGER_.log(Level.INFO, "Writing to the network");
        try {
            buffer_.flip();
            channel_.write(buffer_);
            key_.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not write to socket channel");
            throw new RuntimeException(e);
        }
    }
}
