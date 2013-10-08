package com.company.database;

import com.company.exception.ClientCreationException;
import com.company.exception.MessageCreationException;
import com.company.exception.QueueCreationException;
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

    private void init() {
        datasource_.connect();
    }

    public void createClient(Client c) throws ClientCreationException {
        datasource_.createClient(c);
    }

    public void deleteClient(Client c) {
        datasource_.deleteClient(c);
    }

    public void createQueue(Queue q) throws QueueCreationException {
            datasource_.createQueue(q);
    }

    public void deleteQueue(Queue q) {
        datasource_.deleteQueue(q);
    }

    public void createMessage(Message m) throws MessageCreationException {
        datasource_.createMessage(m);
    }
}
