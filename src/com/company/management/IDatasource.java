package com.company.management;

import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Queue;
import com.company.model.Message;

import java.util.List;

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
    void enqueueMessage(Message m) throws MessageEnqueueingException;
    Message dequeueMessage(Queue q, boolean highestPriority) throws MessageDequeueingException;
    Message dequeueMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeueingException;
    Message peekMessage(Queue q, boolean highestPriority) throws MessagePeekingException;
    Message peekMessage(Queue q, Client c, boolean highestPriority) throws MessagePeekingException;

    int getClientCount() throws GetCountException;
    int getQueueCount() throws GetCountException;
    int getMessageCount() throws GetCountException;
    List<Queue> getAllQueues() throws GetAllQueuesException;
    List<Message> getAllMessagesFromQueue(int id) throws GetAllMessagesFromQueueException;
}
