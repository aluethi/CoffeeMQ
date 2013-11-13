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
public class Connection implements Runnable {

    private static Logger LOGGER_ = Logger.getLogger(Connection.class.getCanonicalName());

    private final ByteBuffer buffer_;
    private final ICallback callback_;

    public Connection(ByteBuffer buffer, ICallback callback) {
        buffer_ = buffer;
        callback_ = callback;
    }

    /**
     * Executing the incoming message on the ExecutionEngine.
     */
    @Override
    public void run() {
        LOGGER_.log(Level.INFO, "Running executor: " + Thread.currentThread().getId());
        ExecutionEngine engine = new ExecutionEngine();
        engine.process(buffer_);

        // calling the callback to register a write interest on the selector.
        callback_.callback();
    }
}
