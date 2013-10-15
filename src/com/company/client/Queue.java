package com.company.client;

import com.company.exception.MsgInsertionException;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 14.10.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class Queue {

    private final MessageServiceImpl msgService_;
    private int id_;

    public Queue(MessageServiceImpl msgService, int id) {
        msgService_ = msgService;
        id_ = id;
    }

    public int getId() {
        return id_;
    }

    public void put(Message msg) throws MsgInsertionException {
        msgService_.put(id_, msg);
    }

    //Gets oldest message from queue
    public Message get() {
        return null;
    }

    //Gets oldest message from sender with id senderId from queue
    public Message getFromSender(int senderId) {
        return null;
    }

    //Gets oldest message with highest priority from queue
    public Message getHighestPriority() {
        return null;
    }

    //Gets oldest message from sender with id senderId with highest priority from queue
    public Message getFromSenderHighestPriority(int senderId) {
        return null;
    }

    public Message peek() {
        return null;
    }
}
