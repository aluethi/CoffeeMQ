package com.company.client;

import com.company.exception.*;

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

    public void put(Message msg) throws SenderDoesNotExistException, QueueDoesNotExistException, MessageEnqueueingException {
        msgService_.put(id_, msg);
    }

    //Gets oldest message from queue
    public Message get() throws NoMessageInQueueException, NoMessageFromSenderException, MessageDequeueingException, QueueDoesNotExistException {
        return msgService_.get(id_, 0, false);
    }

    //Gets oldest message from sender with id senderId from queue
    public Message getFromSender(int senderId) throws NoMessageInQueueException, NoMessageFromSenderException, MessageDequeueingException, QueueDoesNotExistException {
        return msgService_.get(id_, senderId, false);
    }

    //Gets oldest message with highest priority from queue
    public Message getHighestPriority() throws NoMessageInQueueException, NoMessageFromSenderException, MessageDequeueingException, QueueDoesNotExistException {
        return msgService_.get(id_, 0, true);
    }

    //Gets oldest message from sender with id senderId with highest priority from queue
    public Message getFromSenderHighestPriority(int senderId) throws NoMessageInQueueException, NoMessageFromSenderException, MessageDequeueingException, QueueDoesNotExistException {
        return msgService_.get(id_, senderId, true);
    }

    //Peeks (gets message without deleting it) oldest message from queue
    public Message peek() throws NoMessageInQueueException, MessagePeekingException, QueueDoesNotExistException, NoMessageFromSenderException {
        return msgService_.peek(id_, 0, false);
    }

    //Peeks (gets message without deleting it) oldest message from sender with id senderId from queue
    public Message peekFromSender(int senderId) throws NoMessageInQueueException, MessagePeekingException, QueueDoesNotExistException, NoMessageFromSenderException {
        return msgService_.peek(id_, senderId, false);
    }

    //Peeks (gets message without deleting it) oldest message with highest priority from queue
    public Message peekHighestPriority() throws NoMessageInQueueException, MessagePeekingException, QueueDoesNotExistException, NoMessageFromSenderException {
        return msgService_.peek(id_, 0, true);
    }

    //Peeks (gets message without deleting it) oldest message from sender with id senderId with highest priority from queue
    public Message peekFromSenderHighestPriority(int senderId) throws NoMessageInQueueException, MessagePeekingException, QueueDoesNotExistException, NoMessageFromSenderException {
        return msgService_.peek(id_, senderId, true);
    }
}
