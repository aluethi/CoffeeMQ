package com.company.database;

import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
            CallableStatement cst = con.prepareCall("{ call createClient(?, ?) }");
            cst.setInt(1, c.getId());
            cst.setTimestamp(2, c.getCreated());
            cst.execute();
            con.commit();
            cst.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while creating a client: " + e);
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
            CallableStatement cst = con.prepareCall("{ call deleteClient(?) }");
            cst.setInt(1, c.getId());
            cst.execute();
            con.commit();
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
            CallableStatement cst = con.prepareCall("{ call createQueue(?, ?) }");
            cst.setInt(1, q.getId());
            cst.setTimestamp(2, q.getCreated());
            cst.execute();
            con.commit();
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
    public Queue getQueue(int id) throws GetQueueException {
        //Insert queue q into table 'Queue'
        Connection con = PGConnectionPool.getInstance().getConnection();
        ResultSet rs;
        Queue q = null;
        try {
            CallableStatement cst = con.prepareCall("{ call getQueue(?) }");
            cst.setInt(1, id);
            rs = cst.executeQuery();
            if (rs.next()) {
                int qid = rs.getInt(1);
                Timestamp created = rs.getTimestamp(2);
                q = ModelFactory.createQueue(qid, created);
            }
            cst.close();
            rs.close();

        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while getting a queue: " + e);
            throw new GetQueueException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection. " + e);
                throw new RuntimeException(e);
            }
        }
        return q;
    }

    @Override
    public void deleteQueue(Queue q) throws QueueDeletionException {
        //Delete queue q from table 'Queue'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call deleteQueue(?) }");
            cst.setInt(1, q.getId());
            cst.execute();
            con.commit();
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
    public void enqueueMessage(Message m) throws MessageEnqueueingException {
        //Insert message m into table 'Message'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call enqueueMessage(?, ?, ?, ?, ?, ?, ?) }");
            cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            ResultSet rs = cst.executeQuery();
            //TODO: remove
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
            con.commit();
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while enqueuing a message");
            throw new MessageEnqueueingException(e);
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
    public Message dequeueMessage(Queue q, boolean highestPriority) throws MessageDequeueingException {
        //Get and delete oldest message m in queue q from table 'Message' in case highestPriority = false
        //Get and delete message m with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        CallableStatement cst;
        ResultSet rs;
        Message m = null;
        try {
            if (!highestPriority) {
                cst = con.prepareCall("{ call dequeueOldestMessage(?) }");
            } else {
                cst = con.prepareCall("{ call dequeueOldestMessageWithHighestPriority(?) }");
            }
            cst.setInt(1, q.getId());
            rs = cst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                int sender = rs.getInt(2);
                int receiver = rs.getInt(3);
                int queue = rs.getInt(4);
                int context = rs.getInt(5);
                int priority = rs.getInt(6);
                Timestamp created = rs.getTimestamp(7);
                String message = rs.getString(8);
                m = ModelFactory.createMessage(id, sender, receiver, queue, context, priority, created, message);
            }
            con.commit();
            cst.close();
            rs.close();

        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while dequeuing a message");
            throw new MessageDequeueingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }

        return m;
    }

    @Override
    public Message dequeueMessage(Queue q, Client c, boolean highestPriority) throws MessageDequeueingException {
        //Get and delete oldest message m from sender c in queue q from table 'Message' in case highestPriority = false
        //Get and delete message m from sender c with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        CallableStatement cst;
        ResultSet rs;
        Message m = null;
        try {
            if (!highestPriority) {
                cst = con.prepareCall("{ call dequeueOldestMessageFromSender(?, ?) }");
            } else {
                cst = con.prepareCall("{ call dequeueOldestMessageFromSenderWithHighestPriority(?, ?) }");
            }
            cst.setInt(1, q.getId());
            cst.setInt(2, c.getId());
            rs = cst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                int sender = rs.getInt(2);
                int receiver = rs.getInt(3);
                int queue = rs.getInt(4);
                int context = rs.getInt(5);
                int priority = rs.getInt(6);
                Timestamp created = rs.getTimestamp(7);
                String message = rs.getString(8);
                m = ModelFactory.createMessage(id, sender, receiver, queue, context, priority, created, message);
            }
            con.commit();
            cst.close();
            rs.close();

        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while dequeuing a message");
            throw new MessageDequeueingException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection.");
                throw new RuntimeException(e);
            }
        }
        return m;
    }

    @Override
    public Message peekMessage(Queue q, boolean highestPriority) throws MessagePeekingException {
        //Get oldest message m in queue q from table 'Message' in case highestPriority = false
        //Get message m with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        CallableStatement cst;
        ResultSet rs;
        Message m = null;
        try {
            if (!highestPriority) {
                cst = con.prepareCall("{ call peekOldestMessage(?) }");
            } else {
                cst = con.prepareCall("{ call peekOldestMessageWithHighestPriority(?) }");
            }
            cst.setInt(1, q.getId());
            rs = cst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                int sender = rs.getInt(2);
                int receiver = rs.getInt(3);
                int queue = rs.getInt(4);
                int context = rs.getInt(5);
                int priority = rs.getInt(6);
                Timestamp created = rs.getTimestamp(7);
                String message = rs.getString(8);
                m = ModelFactory.createMessage(id, sender, receiver, queue, context, priority, created, message);
            }
            cst.close();
            rs.close();

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

        return m;
    }

    @Override
    public Message peekMessage(Queue q, Client c, boolean highestPriority) throws MessagePeekingException {
        //Get oldest message m from sender c in queue q from table 'Message' in case highestPriority = false
        //Get message m from sender c with highest priority in queue q from table 'Message' in case highestPriority = true
        Connection con = PGConnectionPool.getInstance().getConnection();
        CallableStatement cst;
        ResultSet rs;
        Message m = null;
        try {
            if (!highestPriority) {
                cst = con.prepareCall("{ call peekOldestMessageFromSender(?, ?) }");
            } else {
                cst = con.prepareCall("{ call peekOldestMessageFromSenderWithHighestPriority(?, ?) }");
            }
            cst.setInt(1, q.getId());
            cst.setInt(2, c.getId());
            rs = cst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                int sender = rs.getInt(2);
                int receiver = rs.getInt(3);
                int queue = rs.getInt(4);
                int context = rs.getInt(5);
                int priority = rs.getInt(6);
                Timestamp created = rs.getTimestamp(7);
                String message = rs.getString(8);
                m = ModelFactory.createMessage(id, sender, receiver, queue, context, priority, created, message);
            }
            cst.close();
            rs.close();

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
        return m;
    }

    @Override
    public int getClientCount() throws GetCountException {
        //Get client count
        Connection con = PGConnectionPool.getInstance().getConnection();
        ResultSet rs;
        int count = -1;
        try {
            CallableStatement cst = con.prepareCall("{ call getClientCount() }");
            rs = cst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while getting the client count: " + e);
            throw new GetCountException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection. " + e);
                throw new RuntimeException(e);
            }
        }
        return count;
    }

    @Override
    public int getQueueCount() throws GetCountException {
        //Get queue count
        Connection con = PGConnectionPool.getInstance().getConnection();
        ResultSet rs;
        int count = -1;
        try {
            CallableStatement cst = con.prepareCall("{ call getQueueCount() }");
            rs = cst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while getting the queue count: " + e);
            throw new GetCountException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection. " + e);
                throw new RuntimeException(e);
            }
        }
        return count;
    }

    @Override
    public int getMessageCount() throws GetCountException {
        //Get message count
        Connection con = PGConnectionPool.getInstance().getConnection();
        ResultSet rs;
        int count = -1;
        try {
            CallableStatement cst = con.prepareCall("{ call getMessageCount() }");
            rs = cst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while getting the message count: " + e);
            throw new GetCountException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection. " + e);
                throw new RuntimeException(e);
            }
        }
        return count;
    }

    @Override
    public List<Queue> getAllQueues() throws GetAllQueuesException {
        //Get all queues in the system
        Connection con = PGConnectionPool.getInstance().getConnection();
        ResultSet rs;
        List<Queue> queues = new ArrayList<Queue>();
        try {
            CallableStatement cst = con.prepareCall("{ call getAllQueues() }");
            rs = cst.executeQuery();
            while (rs.next()) {
               queues.add(ModelFactory.createQueue(rs.getInt(1), rs.getTimestamp(2)));
            }
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while getting all queues: " + e);
            throw new GetAllQueuesException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection. " + e);
                throw new RuntimeException(e);
            }
        }
        return queues;
    }

    @Override
    public List<Message> getAllMessagesFromQueue(int id) throws GetAllMessagesFromQueueException {
        //Get all message from queue with id
        Connection con = PGConnectionPool.getInstance().getConnection();
        ResultSet rs;
        List<Message> messages = new ArrayList<Message>();
        try {
            CallableStatement cst = con.prepareCall("{ call getAllMessagesFromQueue(?) }");
            cst.setInt(1, id);
            rs = cst.executeQuery();
            while (rs.next()) {
                messages.add(ModelFactory.createMessage(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getTimestamp(7), rs.getString(8)));
            }
            cst.close();
            rs.close();
        } catch (SQLException e) {
            LOGGER_.log(Level.WARNING, "There was an error while getting all messages from a queue: " + e);
            throw new GetAllMessagesFromQueueException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER_.log(Level.SEVERE, "Error while closing the database connection. " + e);
                throw new RuntimeException(e);
            }
        }
        return messages;
    }

}
