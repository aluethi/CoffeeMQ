package com.company.database;

import com.company.exception.ClientCreationException;
import com.company.exception.MessageCreationException;
import com.company.exception.QueueCreationException;
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
    void deleteClient(Client c);
    void createQueue(Queue q) throws QueueCreationException;
    void deleteQueue(Queue q);
    void createMessage(Message m) throws MessageCreationException;

    void connect();
    void disconnect();
}
