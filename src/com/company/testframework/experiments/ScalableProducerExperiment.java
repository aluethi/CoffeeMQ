package com.company.testframework.experiments;

import com.company.client.Message;
import com.company.client.MessageService;
import com.company.client.Queue;
import com.company.exception.MsgInsertionException;
import com.company.exception.NonExistentQueueException;
import com.company.exception.RegisterFailureException;
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

    public ScalableProducerExperiment() {

    }

    @Override
    public void setUp(String[] args) {
        HOST_ = args[1];
        producerCount_ = Integer.parseInt(args[2]);
        for (int i = 0; i < producerCount_; i++) {
            (new Thread(new Producer("Producer" + i, "Queue" + i, 50, 25))).start();
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

    public class Producer implements Runnable {

        MessageService service_;
        Queue q_;

        Random r = new Random();
        int thinkTimeMean_;
        int thinkTimeSd_;
        String queueId_;

        public Producer(String clientName, String queueId, int thinkTimeMean, int thinkTimeSd) {
            thinkTimeMean_ = thinkTimeMean;
            thinkTimeSd_ = thinkTimeSd;
            queueId_ = queueId;
            service_ = new MessageService(HOST_, PORT_);

            try {
                service_.register(clientName);
                q_ = service_.createQueue(queueId_);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NonExistentQueueException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            /*boolean success = false;
            while (!success) {
                try {
                    service_.register(clientName);
                    q_ = service_.createQueue(queueId_);
                    success = true;
                } catch (RegisterFailureException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (NonExistentQueueException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }*/

        }

        @Override
        public void run() {
            double thinkTime = 0;
            Message msg = new Message(0,0,0,"This is a message");
            while (true) {
                try {
                    q_.put(msg);
                } catch (MsgInsertionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

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
