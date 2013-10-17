package com.company.client;

import com.company.core.MQProtocol;
import com.company.exception.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 14.10.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class MessageServiceImpl {

    private static Logger LOGGER_ = Logger.getLogger(MessageServiceImpl.class.getCanonicalName());

    private Socket socket_;
    private DataInputStream in_;
    private DataOutputStream out_;

    private String host_;
    private int port_;

    private int clientId_;

    public MessageServiceImpl(String host, int port) {
        host_ = host;
        port_ = port;
    }

    public void init(String host, int port) {
        try {
            socket_ = new Socket(host, port);
            in_ = new DataInputStream(socket_.getInputStream());
            out_ = new DataOutputStream(socket_.getOutputStream());
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open socket. Stopping.");
            throw new RuntimeException(e);
        }
    }

    private void tearDown() throws IOException {
        in_.close();
        out_.close();
        socket_.close();
    }

    public void register(String clientId) throws RegisterFailureException {
        init(host_, port_);
        clientId_ = clientId.hashCode();
        register(clientId_);
    }

    public void register(int clientId) throws RegisterFailureException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(MQProtocol.MSG_REGISTER);
            out_.writeInt(clientId);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new RegisterFailureException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void deregister() throws DeregisterFailureException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(MQProtocol.MSG_DEREGISTER);
            out_.writeInt(clientId_);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new DeregisterFailureException();
            }
            tearDown();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Queue createQueue(String queueId) throws NonExistentQueueException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(MQProtocol.MSG_CREATE_QUEUE);
            out_.writeInt(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new NonExistentQueueException();
            }
            return new Queue(this, queueId.hashCode());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Queue getQueue(String queueId) throws NonExistentQueueException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(MQProtocol.MSG_GET_QUEUE);
            out_.writeInt(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new NonExistentQueueException();
            }
            return new Queue(this, queueId.hashCode());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public void deleteQueue(String queueId) throws NonExistentQueueException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(MQProtocol.MSG_DELETE_QUEUE);
            out_.writeInt(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new NonExistentQueueException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void put(List<Queue> queues, Message msg) {
        //TODO
    }

    public List<Queue> getWaitingQueues() {
        //TODO
        return null;
    }

    public void put(int queueId, Message msg) throws MsgInsertionException {
        try {
            out_.writeInt(28 + msg.getMessage().getBytes().length); //Size
            out_.writeInt(MQProtocol.MSG_PUT_INTO_QUEUE);
            out_.writeInt(queueId); //Queue
            out_.writeInt(clientId_); //Sender
            out_.writeInt(msg.getReceiver()); //Receiver
            out_.writeInt(msg.getContext());
            out_.writeInt(msg.getPriority());
            out_.writeInt(msg.getMessage().getBytes().length);
            out_.write(msg.getMessage().getBytes());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new MsgInsertionException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    //Gets/peeks message from queue
    public Message get(int queueId, int senderId, boolean highestPriority, boolean peek) throws MsgRetrievalException {

        int getType = peek ? MQProtocol.MSG_PEEK : MQProtocol.MSG_GET;
        int prio = highestPriority ? 1 : 0;


        try {
            /*if (!peek) {
                if (senderId == 0) {
                    if (!highestPriority) {
                        out_.writeInt(MQProtocol.MSG_GET_FROM_QUEUE);
                    } else {
                        out_.writeInt(MQProtocol.MSG_GET_FROM_QUEUE_HIGHESTPRIORITY);
                    }
                } else {
                    if (!highestPriority) {
                        out_.writeInt(MQProtocol.MSG_GET_FROM_QUEUE_FROMSENDER);
                        out_.writeInt(senderId);
                    } else {
                        out_.writeInt(MQProtocol.MSG_GET_FROM_QUEUE_FROMSENDER_HIGHESTPRIORITY);
                        out_.writeInt(senderId);
                    }
                }
            } else {
                if (senderId == 0) {
                    if (!highestPriority) {
                        out_.writeInt(MQProtocol.MSG_PEEK_FROM_QUEUE);
                    } else {
                        out_.writeInt(MQProtocol.MSG_PEEK_FROM_QUEUE_HIGHESTPRIORITY);
                    }
                } else {
                    if (!highestPriority) {
                        out_.writeInt(MQProtocol.MSG_PEEK_FROM_QUEUE_FROMSENDER);
                        out_.writeInt(senderId);
                    } else {
                        out_.writeInt(MQProtocol.MSG_PEEK_FROM_QUEUE_FROMSENDER_HIGHESTPRIORITY);
                        out_.writeInt(senderId);
                    }
                }

            }*/

            out_.writeInt(16); //Size
            out_.writeInt(getType);
            out_.writeInt(senderId);
            out_.writeInt(prio);
            out_.writeInt(queueId);
            out_.flush();

            //Read data
            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                int errorCode = in_.readInt();
                throw new MsgRetrievalException();
            }
            int sender = in_.readInt();
            int receiver = in_.readInt();
            int context = in_.readInt();
            int priority = in_.readInt();
            int msgLength = in_.readInt();
            byte[] message = new byte[msgLength];
            in_.read(message, 0, msgLength);

           //Create object
            Message msg = new Message(receiver, context, priority, new String(message));
            msg.setSender(sender);
            return msg;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
