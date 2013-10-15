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

    private int clientId_;

    public MessageServiceImpl(String host, int port) {
        init(host, port);
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

    public void register(String clientId) throws RegisterFailureException {
        clientId_ = clientId.hashCode();
        register(clientId_);
    }

    public void register(int clientId) throws RegisterFailureException {
        try {
            out_.write(MQProtocol.MSG_REGISTER);
            out_.write(clientId);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                throw new RegisterFailureException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void deregister() throws DeregisterFailureException {
        try {
            out_.write(MQProtocol.MSG_DEREGISTER);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                throw new DeregisterFailureException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Queue getQueue(String queueId) throws NonExistentQueueException {
        try {
            out_.write(MQProtocol.MSG_GET_QUEUE);
            out_.write(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
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
            out_.write(MQProtocol.MSG_DELETE_QUEUE);
            out_.write(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
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
            out_.write(MQProtocol.MSG_PUT_INTO_QUEUE);
            out_.write(queueId); //Queue
            out_.write(clientId_); //Sender
            out_.write(msg.getReceiver()); //Receiver
            out_.write(msg.getContext());
            out_.write(msg.getPriority());
            out_.write(msg.getMessage().length());
            out_.write(msg.getMessage().getBytes());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                throw new MsgInsertionException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    //Gets oldest message from queue
    public Message get(Queue q) {
        try {
            out_.write(MQProtocol.MSG_GET_FROM_QUEUE);
            //TODO: Write out name of queue
            out_.flush();

           //TODO: Read data and create obj
            return null;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    //Gets oldest message from sender with id senderId from queue
    public Message getFromSender(Queue q, int senderId) {
        return null;
    }

    //Gets oldest message with highest priority from queue
    public Message getHighestPriority(Queue q) {
        return null;
    }

    //Gets oldest message from sender with id senderId with highest priority from queue
    public Message getFromSenderHighestPriority(Queue q, int senderId) {
        return null;
    }

    //Peeks (read without delete) oldest message from queue
    public Message peek(Queue q) {
        return null;
    }

    //Peeks (read without delete) oldest message from sender with id senderId from queue
    public Message peekFromSender(Queue q, int senderId) {
        return null;
    }

    //Peeks (read without delete) oldest message with highest priority from queue
    public Message peekHighestPriority(Queue q) {
        return null;
    }

    //Peeks (read without delete) oldest message from sender with id senderId with highest priority from queue
    public Message peekFromSenderHighestPriority(Queue q, int senderId) {
        return null;
    }

}
