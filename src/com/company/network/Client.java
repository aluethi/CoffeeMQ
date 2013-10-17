package com.company.network;

import com.company.core.ExecutionEngine;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/14/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client implements Runnable {

    private static Logger LOGGER_ = Logger.getLogger(Client.class.getCanonicalName());

    private final ByteBuffer buffer_;
    private final ICallback callback_;

    public Client(ByteBuffer buffer, ICallback callback) {
        buffer_ = buffer;
        callback_ = callback;
    }

    @Override
    public void run() {
        LOGGER_.log(Level.INFO, "Processing client");
        ExecutionEngine engine = new ExecutionEngine();
        engine.process(buffer_);
        LOGGER_.log(Level.INFO, "Processing done");
        callback_.callback();
    }
}
