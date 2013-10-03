package com.company.database;

import com.company.model.Message;
import com.company.model.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IDatasource {
    void createQueue(Queue q);
    void deleteQueue(Queue q);
    void putMessage(Queue q, Message m);

    void connect();
    void disconnect();
}
