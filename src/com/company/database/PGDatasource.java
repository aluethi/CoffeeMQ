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
public class PGDatasource implements IDatasource {
    @Override
    public Queue createQueue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteQueue(Queue q) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void putMessage(Queue q, Message m) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connect() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
