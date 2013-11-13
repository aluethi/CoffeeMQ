package com.company.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 07/11/13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class DaoManager {

    private static Logger LOGGER_ = Logger.getLogger(DaoManager.class.getCanonicalName());

    private Connection connection_ = null;
    private ClientDao clientDao_ = null;
    private QueueDao queueDao_ = null;
    private MessageDao messageDao_ = null;

    public void beginConnectionScope() {
        LOGGER_.log(Level.INFO, "Begin connection scope.");
        connection_ = PGConnectionPool.getInstance().getConnection();
    }

    public void endConnectionScope() {
        try {
            LOGGER_.log(Level.INFO, "Ending connection scope.");
            connection_.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.SEVERE, "Could not close DB connection.");
            throw new RuntimeException(e);
        }
    }

    public void beginTransaction() {
        try {
            LOGGER_.log(Level.INFO, "Begin transaction scope.");
            connection_.setAutoCommit(false);
            //connection_.setTransactionIsolation(connection_.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            LOGGER_.log(Level.SEVERE, "Could not begin transaction.");
            throw new RuntimeException(e);
        }
    }

    public void endTransaction() {
        try {
            LOGGER_.log(Level.INFO, "Ending transaction scope.");
            connection_.commit();
        } catch (SQLException e) {
            LOGGER_.log(Level.SEVERE, "Could not end transaction.");
            throw new RuntimeException(e);
        }
    }

    public void abortTransaction() {
        try {
            LOGGER_.log(Level.INFO, "Aborting transaction.");
            connection_.rollback();
        } catch (SQLException e) {
            LOGGER_.log(Level.SEVERE, "Could not abort transaction.");
            throw new RuntimeException(e);
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
