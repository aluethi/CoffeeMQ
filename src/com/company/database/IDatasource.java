package com.company.database;

import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Queue;
import com.company.model.Message;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IDatasource {
    void createClient(Client c) throws ClientCreationException;
    void deleteClient(Client c) throws ClientDeletionException;
    void createQueue(Queue q) throws QueueCreationException;
    Queue getQueue(int id) throws GetQueueException;
    void deleteQueue(Queue q) throws QueueDeletionException;
    void enqueueMessage(Message m) throws MessageEnqueuingException;
    Message dequeueMessage(Queue q, boolean highestPriority) throws MessageDequeuingException;
    Message dequeueMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeuingException;
    Message peekMessage(Queue q, boolean highestPriority) throws MessagePeekingException;
    Message peekMessage(Queue q, Client c, boolean highestPriority) throws MessagePeekingException;
}
