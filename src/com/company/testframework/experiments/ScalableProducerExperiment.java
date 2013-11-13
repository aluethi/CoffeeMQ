package com.company.testframework.experiments;

import com.company.client.Message;
import com.company.client.MessageService;
import com.company.client.Queue;
import com.company.exception.*;
import com.company.logger.Logger;
import com.company.logger.LoggerSingleton;
import com.company.testframework.Experiment;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 08.11.13
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class ScalableProducerExperiment extends Experiment {

    public static final int PORT_ = 5555;
    public  String HOST_ = "localhost";
    public int producerCount_ = 0;
    public Logger logger_ = LoggerSingleton.getLogger();

    @Override
    public void setUp(String[] args) {
        HOST_ = args[1];
        producerCount_ = Integer.parseInt(args[3]);
        for (int i = 0; i < producerCount_; i++) {
            try {
                (new Thread(new Producer(args[2] + "_Producer" + i, "Queue", 50, 25))).start();
            } catch (QueueDoesNotExistException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueReadException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public void tearDown() {

    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public class Producer implements Runnable {

        MessageService service_;
        Queue q_;

        Random r = new Random();
        int thinkTimeMean_;
        int thinkTimeSd_;
        String queueId_;

        public Producer(String clientName, String queueId, int thinkTimeMean, int thinkTimeSd) throws QueueDoesNotExistException, QueueReadException {
            thinkTimeMean_ = thinkTimeMean;
            thinkTimeSd_ = thinkTimeSd;
            queueId_ = queueId;
            service_ = new MessageService(HOST_, PORT_);

            try {
                service_.register(clientName);
                q_ = service_.createQueue(queueId_);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueCreationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueExistsException e) {
                try {
                    q_ = service_.getQueue(queueId_);
                } catch (QueueReadException e2) {
                    e2.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e2) {
                    e2.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } catch (ClientExistsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        @Override
        public void run() {
            double thinkTime = 0;
            Message msg = new Message(0,0,0,"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                                            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                                            "12345678901234567890123456789012345678901234567890");
            long starttime = 0, stoptime = 0;
            while (true) {
                try {
                    starttime = System.nanoTime();
                    q_.put(msg);
                    stoptime = System.nanoTime();
                } catch (SenderDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MessageEnqueueingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    continue;
                }
                logger_.log(starttime + "," + stoptime + ",PUT");

                // Do something
                thinkTime = thinkTimeMean_ + (r.nextGaussian() * thinkTimeSd_);
                if (thinkTime < 0)
                    thinkTime = 0;
                try {
                    Thread.sleep(Math.round(thinkTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}
