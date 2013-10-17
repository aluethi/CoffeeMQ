package com.company;

import com.company.client.Message;
import com.company.client.MessageService;
import com.company.client.Queue;
import com.company.exception.*;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainClient implements Runnable {

    private Random rand_ = new Random();
    private final int clientCount_;
    private final int runningTime_;
    private Consumer[] consumers_;
    private Producer[] producers_;
    private String host_;
    private int port_;

    public static void main(String[] args) {
        String host = "";
        MainClient m = new MainClient(host, 5555, 25, 60*1);
        new Thread(m).start();
    }

    public MainClient(String host, int port, int clientCount, int runningTime) {
        host_ = host;
        port_ = port;
        clientCount_ = clientCount;
        runningTime_ = runningTime;
    }

    private void setUp() {
        consumers_ = new Consumer[clientCount_];
        producers_ = new Producer[clientCount_];

        MessageService service = new MessageService(host_, port_);
        try {
            service.register("service client");
            for(int i = 0; i < clientCount_; i++)
                service.createQueue("queue" + i);
            service.deregister();
        } catch (RegisterFailureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NonExistentQueueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DeregisterFailureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for(int i = 0; i < clientCount_; i++) {
            producers_[i] = new Producer(host_, port_, "client"+(i*2), "queue"+i);
            consumers_[i] = new Consumer(host_, port_, "client"+(i*2+1), "queue"+i);
            new Thread(producers_[i]).start();
            new Thread(consumers_[i]).start();
        }
    }

    private void tearDown() {
        for(int i = 0; i < clientCount_; i++) {
            producers_[i].shutdown();
            consumers_[i].shutdown();
        }
    }

    @Override
    public void run() {
        setUp();
        long endTime = System.currentTimeMillis() + runningTime_ * 1000;
        while(endTime > System.currentTimeMillis()) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        tearDown();
    }

    public abstract class ClientThread implements Runnable {

        protected volatile boolean isRunning_ = true;

        protected final String clientId_;
        protected final String queueId_;
        protected final String host_;
        protected final int port_;
        protected MessageService service_;
        protected Queue q_;

        public ClientThread(String host, int port, String clientId, String queueId) {
            clientId_ = clientId;
            queueId_ = queueId;
            port_ = port;
            host_ = host;
            init();
        }

        protected void init() {
            service_ = new MessageService(host_, port_);
            try {
                service_.register(clientId_);
                q_ = service_.getQueue(queueId_);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NonExistentQueueException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        protected void tearDown() {
            try {
                service_.deregister();
            } catch (DeregisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public void shutdown() {
            isRunning_ = false;
        }
    }

    public class Consumer extends ClientThread {

        public int consumeCounter = 0;

        public Consumer(String host, int port, String clientId, String queueId) {
            super(host, port, clientId, queueId);
        }

        @Override
        public void run() {
            while(isRunning_) {
                try {
                    Message m = q_.get();
                    consumeCounter++;
                    int thinkTime = rand_.nextInt(500) + 500;
                    wait(thinkTime);
                } catch (MsgRetrievalException e) {
                    System.out.println("Message could not be retrieved.");
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            tearDown();
        }
    }

    public class Producer extends ClientThread {

        public int producedCounter = 0;

        public Producer(String host, int port, String clientId, String queueId) {
            super(host, port, clientId, queueId);
        }

        @Override
        public void run() {
            while(isRunning_) {
            Message m = new Message(0, 0, 0, "Hallo Herr Klaus, wie gehts Ihrer Katze nach dem Unfall mit der Motorsäge? (Msg: " + producedCounter + ")");
                try {
                    q_.put(m);
                    producedCounter++;
                    int thinkTime = rand_.nextInt(500) + 500;
                    wait(thinkTime);
                } catch (MsgInsertionException e) {
                    System.out.println("Message could not be inserted.");
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            tearDown();
        }
    }

}
