package com.company.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/14/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionHandler implements Runnable {

    private static Logger LOGGER_ = Logger.getLogger(ConnectionHandler.class.getCanonicalName());

    private final SocketChannel channel_;
    private final SelectionKey key_;
    private final ExecutorService executor_;
    private ByteBuffer buffer_;

    public ConnectionHandler(SelectionKey key, SocketChannel channel, ExecutorService executor) {
        key_ = key;
        channel_ = channel;
        executor_ = executor;
        buffer_ = ByteBuffer.allocate(2048);
    }

    /**
     * This method either executes a network read or write depending on the registered interests
     * on the socket channel.
     */
    @Override
    public void run() {
        if(key_.isReadable()) {
            LOGGER_.log(Level.INFO, "read()");
            read();
        } else if(key_.isWritable()) {
            LOGGER_.log(Level.INFO, "write()");
            write();
        }
    }

    /**
     * Reads a message from the network. At first we're reading in an int containing the size of the
     * following message. The complete message is then dispatched to an executor service.
     */
    public void read() {
        try {
            int bytesRead = 0, limit, pos, size;

            // clear buffer
            buffer_.clear();

            // read first message size
            do {
                bytesRead += channel_.read(buffer_);
            } while(bytesRead < 4);

            // safe buffer limit and pos
            limit = buffer_.limit();
            pos = buffer_.position();

            // read message size from buffer
            buffer_.flip();
            size = buffer_.getInt() + 4;

            // restore limit and pos
            buffer_.limit(limit);
            buffer_.position(pos);

            // read more from the network..
            while(bytesRead < size){
                bytesRead += channel_.read(buffer_);
            }

            // submit a client to the executor service
            buffer_.flip();
            buffer_.position(4);
            executor_.submit(new Connection(buffer_,
                    // register the write back callback
                    new ICallback() {
                        @Override
                        public void callback() {
                            key_.interestOps(SelectionKey.OP_WRITE);
                    }
            }));
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not read from socket channel");
            throw new RuntimeException(e);
        }

    }

    /**
     * This method is called as soon as the callback on the client instance registers a write
     * interest on the selector. We're just sending our buffer back via network.
     * After writing we're re-registering a read interest (if the client wants to send
     * another message).
     */
    public void write() {
        try {
            channel_.write(buffer_);
            key_.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not write to socket channel");
            throw new RuntimeException(e);
        }
    }
}
