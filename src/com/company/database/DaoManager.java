package com.company.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 07/11/13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class DaoManager {

    private Connection connection_ = null;
    private ClientDao clientDao_ = null;
    private QueueDao queueDao_ = null;
    private MessageDao messageDao_ = null;

    public void beginConnectionScope() {
        connection_ = PGConnectionPool.getInstance().getConnection();
    }

    public void endConnectionScope() {
        try {
            connection_.close();
        } catch (SQLException e) {
            // TODO
        }
    }

    public void beginTransaction() {
        try {
            connection_.setAutoCommit(false);
        } catch (SQLException e) {
            // TODO
        }
    }

    public void endTransaction() {
        try {
            connection_.commit();
        } catch (SQLException e) {
            // TODO
        }
    }

    public void abortTransaction() {
        try {
            connection_.rollback();
        } catch (SQLException e) {
            // TODO
        }
    }

    public ClientDao getClientDao() {
        if(clientDao_ == null) {
            clientDao_ = new ClientDao(connection_);
        }
        return clientDao_;
    }

    public QueueDao getQueueDao() {
        if(queueDao_ == null) {
            queueDao_ = new QueueDao(connection_);
        }
        return queueDao_;
    }

    public MessageDao getMessageDao() {
        if(messageDao_ == null) {
            messageDao_ = new MessageDao(connection_);
        }
        return messageDao_;
    }

}
