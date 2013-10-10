package com.company.test.network;

import com.company.network.Acceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/8/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class AcceptorTest {

    private Acceptor acc_;

    @Before
    public void setUp() throws Exception {
        /**
         * Set up an acceptor that listens on localhost:5555.
         */
        Executor executor = Executors.newFixedThreadPool(10);
        acc_ = new Acceptor("localhost", 5555, executor);
        new Thread(acc_).start();
    }

    @After
    public void tearDown() throws Exception {
        acc_.shutdown();
    }

    @Test
    public void testRun() throws Exception {
        // try to connect to the server socket.
        Socket s = new Socket("localhost", 5555);
        assertTrue("testRun() - Socket is not connected.", s.isConnected());
    }
}
