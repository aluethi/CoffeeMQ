package com.company.client;

import com.company.exception.*;

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

    public void register(String clientId) throws RegisterFailureException, ClientExistsException {
        msgService_.register(clientId);
    }

    public void register(int clientId) throws RegisterFailureException, ClientExistsException {
        msgService_.register(clientId);
    }

    public void deregister() throws DeregisterFailureException, ClientDoesNotExistException {
        msgService_.deregister();
    }

    public Queue createQueue(String queueId) throws QueueCreationException, QueueExistsException {
        return msgService_.createQueue(queueId);
    }

    public Queue getQueue(String queueId) throws QueueDoesNotExistException, QueueReadException {
        return msgService_.getQueue(queueId);
    }

    public void deleteQueue(String queueId) throws QueueDoesNotExistException, QueueDeletionException {
        msgService_.deleteQueue(queueId);
    }
}
