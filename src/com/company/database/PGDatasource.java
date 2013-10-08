package com.company.database;

import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.Queue;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class PGDatasource implements IDatasource {

    private static Logger LOGGER_ = Logger.getLogger(PGDatasource.class.getCanonicalName());

    @Override
    public void createClient(Client c) throws ClientCreationException {
        //Insert client c into table 'Client'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call createclient(?, ?) }");
            cst.setInt(1, c.getId());
            cst.setTimestamp(2, c.getCreated());
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while creating a client");
            throw new ClientCreationException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteClient(Client c) throws ClientDeletionException {
        //Delete client c from table 'Client'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call deleteclient(?) }");
            cst.setInt(1, c.getId());
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while deleting a client");
            throw new ClientDeletionException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void createQueue(Queue q) throws QueueCreationException {
        //Insert queue q into table 'Queue'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call createqueue(?, ?) }");
            cst.setInt(1, q.getId());
            cst.setTimestamp(2, q.getCreated());
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while creating a queue");
            throw new QueueCreationException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteQueue(Queue q) throws QueueDeletionException {
        //Delete queue q from table 'Queue'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call deletequeue(?) }");
            cst.setInt(1, q.getId());
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while deleting a queue");
            throw new QueueDeletionException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void enqueueMessage(Message m) throws MessageEnqueuingException {
        //Insert message m into table 'Message'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call enqueuemessage(?, ?, ?, ?, ?, ?, ?) }");
            cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while enqueuing a message");
            throw new MessageEnqueuingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message dequeueMessage(Queue q, boolean highestPriority) throws MessageDequeuingException {
        //Get and delete oldest message m in queue q from table 'Message' in case highestPriority = false
        //Get and delete message m with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call dequeuemessage(?) }");
            /*cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
            cst.close();
            rs.close();*/
            return null;
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while dequeuing a message");
            throw new MessageDequeuingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message dequeueMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeuingException {
        //Get and delete oldest message m from sender c in queue q from table 'Message' in case highestPriority = false
        //Get and delete message m from sender c with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call dequeuemessage(?) }");
            /*cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
            cst.close();
            rs.close();*/
            return null;
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while dequeuing a message");
            throw new MessageDequeuingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message peekMessage(Queue q, boolean highestPriority) throws MessagePeekingException {
        //Get oldest message m in queue q from table 'Message' in case highestPriority = false
        //Get message m with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call peekmessage(?) }");
            /*cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
            cst.close();
            rs.close();*/
            return null;
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while peeking a message");
            throw new MessagePeekingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message peekMessage(Queue q, Client c, boolean highestPriority) throws MessagePeekingException {
        //Get oldest message m from sender c in queue q from table 'Message' in case highestPriority = false
        //Get message m from sender c with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call peekmessage(?) }"); //ToDo
            /*cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
            cst.close();
            rs.close();*/
            return null;
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while peeking a message");
            throw new MessagePeekingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
    }

}
