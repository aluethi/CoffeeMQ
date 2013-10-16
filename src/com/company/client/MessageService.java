package com.company.client;

import com.company.exception.DeregisterFailureException;
import com.company.exception.NonExistentQueueException;
import com.company.exception.RegisterFailureException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageService {

    private final MessageServiceImpl msgService_;

    public MessageService(String host, int port) {
        msgService_ = new MessageServiceImpl(host, port);
    }

    public void register(String clientId) throws RegisterFailureException {
        msgService_.register(clientId);
    }

    public void deregister() throws DeregisterFailureException {
        msgService_.deregister();
    }

    public Queue createQueue(String queueId) throws NonExistentQueueException {
        return msgService_.createQueue(queueId);
    }

    public Queue getQueue(String queueId) throws NonExistentQueueException {
        return msgService_.getQueue(queueId);
    }

    public void deleteQueue(String queueId) throws NonExistentQueueException {
        msgService_.deleteQueue(queueId);
    }
}
