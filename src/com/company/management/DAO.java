package com.company.management;

import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.Queue;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class DAO {
    private final IDatasource datasource_;

    public DAO(IDatasource datasource) {
        datasource_ = datasource;
    }

    public void createClient(Client c) throws ClientCreationException {
        datasource_.createClient(c);
    }

    public void deleteClient(Client c) throws ClientDeletionException {
        datasource_.deleteClient(c);
    }

    public void createQueue(Queue q) throws QueueCreationException {
            datasource_.createQueue(q);
    }

    public Queue getQueue(int id) throws GetQueueException {
        return datasource_.getQueue(id);
    }

    public void deleteQueue(Queue q) throws QueueDeletionException {
        datasource_.deleteQueue(q);
    }

    public void enqueueMessage(Message m) throws MessageEnqueueingException {
        datasource_.enqueueMessage(m);
    }

    public Message dequeueMessage(Queue q, boolean highestPriority) throws MessageDequeueingException {
        return datasource_.dequeueMessage(q, highestPriority);
    }

    public Message dequeueMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeueingException {
        return datasource_.dequeueMessage(q, c, highestPriority);
    }

    public Message peekMessage(Queue q, boolean highestPriority) throws MessageDequeueingException {
        return datasource_.dequeueMessage(q, highestPriority);
    }

    public Message peekMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeueingException {
        return datasource_.dequeueMessage(q, c, highestPriority);
    }

    public int getClientCount() throws GetCountException {
        return datasource_.getClientCount();
    }

    public int getQueueCount() throws GetCountException {
        return datasource_.getQueueCount();
    }

    public int getMessageCount() throws GetCountException {
        return datasource_.getMessageCount();
    }

    public List<Queue> getAllQueues() throws GetAllQueuesException {
        return datasource_.getAllQueues();
    }

    public List<Message> getAllMessagesFromQueue(int id) throws GetAllMessagesFromQueueException {
        return datasource_.getAllMessagesFromQueue(id);
    }
}
