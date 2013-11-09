package com.company.testframework.experiments;

import com.company.client.MessageService;
import com.company.exception.RegisterFailureException;
import com.company.testframework.Experiment;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/19/13
 * Time: 12:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleProducerConsumerExperiment extends Experiment {

    public static final String HOST = "localhost";
    public static final int PORT = 5555;

    @Override
    public void setUp() {
        //service = new MessageService("localhost", 5555);
        System.out.println("Hello World");
    }

    @Override
    public void tearDown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public class Producer implements Runnable {

        MessageService service;

        public Producer() {
        }

        public void setUp(String clientName) {
            try {
                service = new MessageService(HOST, PORT);
                service.register(clientName);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        @Override
        public void run() {

        }
    }

    public class Consumer implements Runnable {

        MessageService service;

        public Consumer() {
        }

        public void setUp(String clientName) {
            try {
                service = new MessageService(HOST, PORT);
                service.register(clientName);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        @Override
        public void run() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
