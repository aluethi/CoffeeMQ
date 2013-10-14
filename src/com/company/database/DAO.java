package com.company.database;

import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.Queue;

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

    public void deleteQueue(Queue q) throws QueueDeletionException {
        datasource_.deleteQueue(q);
    }

    public void enqueueMessage(Message m) throws MessageEnqueuingException {
        datasource_.enqueueMessage(m);
    }

    public Message dequeueMessage(Queue q, boolean highestPriority) throws MessageDequeuingException {
        return datasource_.dequeueMessage(q, highestPriority);
    }

    public Message dequeueMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeuingException {
        return datasource_.dequeueMessage(q, c, highestPriority);
    }

    public Message peekMessage(Queue q, boolean highestPriority) throws MessageDequeuingException {
        return datasource_.dequeueMessage(q, highestPriority);
    }

    public Message peekMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeuingException {
        return datasource_.dequeueMessage(q, c, highestPriority);
    }
}
