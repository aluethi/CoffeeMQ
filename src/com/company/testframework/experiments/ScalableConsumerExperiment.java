package com.company.testframework.experiments;

import com.company.client.MessageService;
import com.company.exception.*;
import com.company.client.*;
import com.company.testframework.Experiment;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 08.11.13
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class ScalableConsumerExperiment extends Experiment {

    public static final int PORT_ = 5555;
    public String HOST_ = "localhost";
    public int consumerCount_ = 0;

    @Override
    public void setUp(String[] args) {
        HOST_ = args[1];
        consumerCount_ = Integer.parseInt(args[3]);
        for (int i = 0; i < consumerCount_; i++) {
            (new Thread(new Consumer(args[2] + "_Consumer" + i, "Queue", 50, 25))).start();
        }
    }

    @Override
    public void tearDown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public class Consumer implements Runnable {

        MessageService service_;
        Queue q_;

        Random r = new Random();
        int thinkTimeMean_;
        int thinkTimeSd_;
        String queueId_;

        public Consumer(String clientName, String queueId, int thinkTimeMean, int thinkTimeSd) {
            thinkTimeMean_ = thinkTimeMean;
            thinkTimeSd_ = thinkTimeSd;
            queueId_ = queueId;
            service_ = new MessageService(HOST_, PORT_);

            try {
                service_.register(clientName);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClientExistsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            boolean success = false;
            while (!success) {
                try {
                    q_ = service_.getQueue(queueId_);
                    success = true;
                } catch (QueueReadException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        @Override
        public void run() {
            double thinkTime = 0;
            Message msg;
            while (true) {
                try {
                    msg = q_.get();
                } catch (NoMessageInQueueException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MessageDequeueingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (NoMessageFromSenderException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                // Do something with the message
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
