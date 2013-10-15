package com.company.client;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 14.10.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class Queue {

    public Queue() {

    }

    public void put(Message msg) {

    }

    public void put(int receiverId, Message msg) {

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
