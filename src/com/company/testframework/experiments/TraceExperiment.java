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
 * User: nano
 * Date: 11/11/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class TraceExperiment extends Experiment {
    public static int PORT_ = 5555;
    public static String HOST_;
    public Logger logger_ = LoggerSingleton.getLogger();

    private int owLocalIntervalBegin, owLocalIntervalEnd;
    private int owRemoteIntervalBegin, owRemoteIntervalEnd;

    private int rrLocalIntervalBegin, rrLocalIntervalEnd;
    private int rrRemoteIntervalBegin, rrRemoteIntervalEnd;

    private int csLocalIntervalBegin, csLocalIntervalEnd;
    private boolean server;

    private double thinkMean, thinkStddev;

    @Override
    public void setUp(String[] args) {
        HOST_ = args[1];
        owLocalIntervalBegin = Integer.parseInt(args[2].split(",")[0]);
        owLocalIntervalEnd = Integer.parseInt(args[2].split(",")[1]);
        owRemoteIntervalBegin = Integer.parseInt(args[3].split(",")[0]);
        owRemoteIntervalEnd = Integer.parseInt(args[3].split(",")[1]);

        rrLocalIntervalBegin = Integer.parseInt(args[4].split(",")[0]);
        rrLocalIntervalEnd = Integer.parseInt(args[4].split(",")[1]);
        rrRemoteIntervalBegin = Integer.parseInt(args[5].split(",")[0]);
        rrRemoteIntervalEnd = Integer.parseInt(args[5].split(",")[1]);

        csLocalIntervalBegin = Integer.parseInt(args[6].split(",")[0]);
        csLocalIntervalEnd = Integer.parseInt(args[6].split(",")[1]);

        server = args[7].equals("1");

        thinkMean = Double.parseDouble(args[8]);
        thinkStddev = Double.parseDouble(args[9]);
    }

    @Override
    public void tearDown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {
        /*
        for(int i = owLocalIntervalBegin; i <= owLocalIntervalEnd; i++) {
            new Thread(new OneWayClient(thinkMean, thinkStddev, i, owRemoteIntervalBegin, owRemoteIntervalEnd)).start();
        }
*/
        for(int i = rrLocalIntervalBegin, j = rrRemoteIntervalBegin; i <= rrLocalIntervalEnd && j <= rrRemoteIntervalEnd; i++, j++) {
            new Thread(new ReqResClient(thinkMean, thinkStddev, i, j)).start();
        }
/*
        for(int i = csLocalIntervalBegin; i <= csLocalIntervalEnd; i++) {
            new Thread(new CSClient(thinkMean, thinkStddev, i, server)).start();
        }
*/
    }

    public class OneWayClient implements Runnable {

        private final int id_, remoteStart_, remoteEnd_;
        private final double thinkMean_, thinkStddev_;
        private MessageService msgService_;
        private Queue queue_;
        private Random r_ = new Random(System.currentTimeMillis());

        public OneWayClient(double thinkMean, double thinkStddev, int id, int remoteStart, int remoteEnd) {
            id_ = id;
            remoteStart_ = remoteStart;
            remoteEnd_ = remoteEnd;
            thinkMean_ = thinkMean;
            thinkStddev_ = thinkStddev;

            msgService_ = new MessageService(HOST_, PORT_);
            try {
                msgService_.register("OneWayClient" + id);
                queue_ = msgService_.createQueue("queue");
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClientExistsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueCreationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueExistsException e) {
                try {
                    queue_ = msgService_.getQueue("queue");
                } catch (QueueDoesNotExistException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueReadException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        @Override
        public void run() {
            double thinkTime;
            long starttime = 0, stoptime = 0;
            Message msg = new Message(0,0,0,"0");
            long put = 0, get = 0;
            while(true) {
                try {
                    starttime = System.nanoTime();
                    if(msg != null)
                        queue_.put(msg);
                    put++;
                    stoptime = System.nanoTime();
                    logger_.log(starttime + "," + stoptime + ",OW_PUT," + id_);
                } catch (SenderDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MessageEnqueueingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                // Do something
                thinkTime = thinkMean_ + (r_.nextGaussian() * thinkStddev_);
                if (thinkTime < 0)
                    thinkTime = 0;
                try {
                    Thread.sleep(Math.round(thinkTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                try {
                    starttime = System.nanoTime();
                    msg = queue_.get();
                    get++;
                    stoptime = System.nanoTime();
                    logger_.log(starttime + "," + stoptime + ",OW_GET," + id_);
                } catch (NoMessageInQueueException e) {
                    msg = null;
                    continue;
                } catch (NoMessageFromSenderException e) {
                    msg = null;
                    continue;
                } catch (MessageDequeueingException e) {
                    msg = null;
                    continue;
                } catch (QueueDoesNotExistException e) {
                    msg = null;
                    continue;
                }

                long count = Integer.parseInt(msg.getMessage());
                msg.setMessage(String.valueOf(++count));
            }
        }
    }

    public class ReqResClient implements Runnable {
        private final int id_, partnerId_;
        private MessageService msgService_;
        private Queue queue_;
        private double thinkMean_, thinkStddev_;
        private Random r_ = new Random();

        public ReqResClient(double thinkMean, double thinkStddev, int id, int partnerId) {
            id_ = id;
            partnerId_ = partnerId;
            thinkMean_ = thinkMean;
            thinkStddev_ = thinkStddev;

            msgService_ = new MessageService(HOST_, PORT_);
            try {
                msgService_.register(id);
                queue_ = msgService_.createQueue("rrq"+(id*partnerId));
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClientExistsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueCreationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueueExistsException e) {
                try {
                    queue_ = msgService_.getQueue("rrq"+(id*partnerId));
                } catch (QueueDoesNotExistException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueReadException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        @Override
        public void run() {
            long starttime, stoptime;
            double thinkTime;
            Message msg = null;

            /*int counter1 = 0, counter2 = 0;

            int count = 0;
            while(true) {
                try {
                    queue_.put(new Message(0, 0, 0, String.valueOf(count)));
                } catch (SenderDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MessageEnqueueingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                // Do something
                thinkTime = thinkMean_ + (r_.nextGaussian() * thinkStddev_);
                if (thinkTime < 0)
                    thinkTime = 0;
                try {
                    Thread.sleep(Math.round(thinkTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                counter1++;
                try {
                    starttime = System.nanoTime();
                    msg = queue_.getFromSender(partnerId_);
                    stoptime = System.nanoTime();
                    //logger_.log(starttime + "," + stoptime + ",RR_GET," + id_);
                } catch (NoMessageInQueueException e) {
                    e.printStackTrace();
                    continue;
                } catch (NoMessageFromSenderException e) {
                    counter2++;
                    e.printStackTrace();
                    continue;
                } catch (MessageDequeueingException e) {
                    e.printStackTrace();
                    continue;
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();
                    continue;
                }

                System.out.println("Counter1: " + counter1 + " Counter2: " + counter2);

                count = Integer.parseInt(msg.getMessage());
                count++;
            }*/


            if(id_ < partnerId_) {
                msg = new Message(partnerId_, 0, 0, "0");
                try {
                    queue_.put(msg);
                    Thread.sleep(500);
                } catch (SenderDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MessageEnqueueingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            while(true) {
                try {
                    System.out.println("ID: " + id_);
                    starttime = System.nanoTime();
                    msg = queue_.getFromSender(partnerId_);
                    stoptime = System.nanoTime();
                    logger_.log(starttime + "," + stoptime + ",RR_GET," + id_);
                } catch (NoMessageInQueueException e) {
                    e.printStackTrace();
                    continue;
                } catch (NoMessageFromSenderException e) {
                    e.printStackTrace();
                    continue;
                } catch (MessageDequeueingException e) {
                    e.printStackTrace();
                    continue;
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();
                    continue;
                }

                long count = Integer.parseInt(msg.getMessage());
                msg = new Message(partnerId_, 0, 0, String.valueOf(++count));

                // Do something
                thinkTime = thinkMean_ + (r_.nextGaussian() * thinkStddev_);
                if (thinkTime < 0)
                    thinkTime = 0;
                try {
                    Thread.sleep(Math.round(thinkTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                try {
                    starttime = System.nanoTime();
                    queue_.put(msg);
                    stoptime = System.nanoTime();
                    logger_.log(starttime + "," + stoptime + ",RR_PUT," + id_);
                } catch (SenderDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueDoesNotExistException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MessageEnqueueingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public class CSClient implements Runnable {
        private final int id_;
        private final double thinkMean_, thinkStddev_;
        private final boolean server_;
        private MessageService msgService_;
        private Queue queue_;
        private Random r_ = new Random(System.currentTimeMillis());

        public CSClient(double thinkMean, double thinkStddev, int id, boolean server) {
            id_ = id;
            thinkMean_ = thinkMean;
            thinkStddev_  = thinkStddev;
            server_ = server;

            msgService_ = new MessageService(HOST_, PORT_);
            try {
                msgService_.register("CSClient" + id + "_" + server);
            } catch (RegisterFailureException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClientExistsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if(server) {
                try {
                    queue_ = msgService_.createQueue("csq"+(id));
                } catch (QueueCreationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueueExistsException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                while(true) {
                    try {
                        queue_ = msgService_.getQueue("csq"+(id));
                    } catch (QueueDoesNotExistException e) {
                        continue;
                    } catch (QueueReadException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    break;
                }
            }
        }

            @Override
        public void run() {
            double thinkTime = 0;
            Message msg;
            if(server_) {
                msg = new Message(0,0,0,"This is a message");
                while (true) {
                    try {
                        long starttime = System.nanoTime();
                        queue_.put(msg);
                        long stoptime = System.nanoTime();
                        logger_.log(starttime + "," + stoptime + ",CS_PUT," + id_);
                    } catch (SenderDoesNotExistException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (QueueDoesNotExistException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (MessageEnqueueingException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    // Do something
                    thinkTime = thinkMean_ + (r_.nextGaussian() * thinkStddev_);
                    if (thinkTime < 0)
                        thinkTime = 0;
                    try {
                        Thread.sleep(Math.round(thinkTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            } else {
                while (true) {
                    try {
                        long starttime = System.nanoTime();
                        msg = queue_.get();
                        long stoptime = System.nanoTime();
                        logger_.log(starttime + "," + stoptime + ",CS_GET," + id_);
                    } catch (NoMessageInQueueException e) {
                        continue;
                    } catch (QueueDoesNotExistException e) {
                        continue;
                    } catch (MessageDequeueingException e) {
                        continue;
                    } catch (NoMessageFromSenderException e) {
                        continue;
                    }

                    // Do something with the message
                    thinkTime = thinkMean_ + (r_.nextGaussian() * thinkStddev_);
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
}
