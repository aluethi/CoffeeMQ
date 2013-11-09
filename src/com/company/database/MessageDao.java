package com.company.database;

import com.company.exception.*;
import com.company.model.Message;
import com.company.model.ModelFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 07/11/13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class MessageDao {

    private Connection con_;

    public MessageDao(Connection con) {
        con_ = con;
    }

    public void enqueueMessage(Message m) throws MessageEnqueueingException, QueueDoesNotExistException, SenderDoesNotExistException {
        try {
            CallableStatement cst = con_.prepareCall("{ call enqueueMessage(?,?,?,?,?,?,?) }");
            cst.setInt(1, m.getSender());
            cst.setInt(2, m.getReceiver());
            cst.setInt(3, m.getQueue());
            cst.setInt(4, m.getContext());
            cst.setInt(5, m.getPriority());
            cst.setTimestamp(6, m.getCreated());
            cst.setString(7, m.getMessage());
            cst.execute();
            cst.close();
        } catch(SQLException e) {
            if(e.getSQLState().equals("V2002")) { // CUSTOM: Client does not exist
                throw new SenderDoesNotExistException(e);
            } else if(e.getSQLState().equals("V2003")) { // CUSTOM: Queue does not exist
                throw new QueueDoesNotExistException(e);
            }
            throw new MessageEnqueueingException(e);
        }
    }

    public Message dequeueMessage(int queueId) throws NoMessageInQueueException, NoMessageFromSenderException, QueueDoesNotExistException, MessageDequeueingException {
        return dequeueMessage(queueId, null, false);
    }

    public Message dequeueMessage(int queueId, boolean highesPriority) throws NoMessageInQueueException, NoMessageFromSenderException, QueueDoesNotExistException, MessageDequeueingException {
        return dequeueMessage(queueId, null, highesPriority);
    }

    public Message dequeueMessage(int queueId, Integer clientId, boolean highestPriority) throws QueueDoesNotExistException, NoMessageInQueueException, NoMessageFromSenderException, MessageDequeueingException {
        CallableStatement cst;
        try {
            if(highestPriority) {
                if(clientId != null) {
                    cst = con_.prepareCall("{ call dequeueOldestMessageFromSenderWithHighestPriority(?, ?) }");
                } else {
                    cst = con_.prepareCall("{ call dequeueOldestMessageWithHighestPriority(?) }");
                }
            } else {
                if(clientId != null) {
                    cst = con_.prepareCall("{ call dequeueOldestMessageFromSender(?, ?) }");
                } else {
                    cst = con_.prepareCall("{ call dequeueOldestMessage(?) }");
                }
            }
            cst.setInt(1, queueId);
            if(clientId != null) {
                cst.setInt(2, clientId);
            }
            ResultSet rs = cst.executeQuery();
            rs.next(); // if result set is empty we get an exception
            Message m = ModelFactory.createMessage(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getTimestamp(7), rs.getString(8));
            rs.close();
            cst.close();
            return m;
        } catch(SQLException e) {
            if(e.getSQLState().equals("V2003")) {
                throw new QueueDoesNotExistException(e);
            } else if (e.getSQLState().equals("V2004")) {
                throw new NoMessageInQueueException(e);
            } else if (e.getSQLState().equals("V2005")) {
                throw new NoMessageFromSenderException(e);
            }
            throw new MessageDequeueingException(e);
        }
    }

    public Message peekMessage(int queueId) throws NoMessageInQueueException, NoMessageFromSenderException, MessagePeekingException, QueueDoesNotExistException {
        return peekMessage(queueId, 0, false);
    }

    public Message peekMessage(int queueId, boolean highestPriority) throws NoMessageInQueueException, NoMessageFromSenderException, MessagePeekingException, QueueDoesNotExistException {
        return peekMessage(queueId, 0, highestPriority);
    }

    public Message peekMessage(int queueId, int clientId, boolean highestPriority) throws QueueDoesNotExistException, NoMessageInQueueException, NoMessageFromSenderException, MessagePeekingException {
        CallableStatement cst;
        try {
            if(highestPriority) {
                if(clientId != 0) {
                    cst = con_.prepareCall("{ call peekOldestMessageFromSenderWithHighestPriority(?, ?) }");
                } else {
                    cst = con_.prepareCall("{ call peekOldestMessageWithHighestPriority(?) }");
                }
            } else {
                if(clientId != 0) {
                    cst = con_.prepareCall("{ call peekOldestMessageFromSender(?, ?) }");
                } else {
                    cst = con_.prepareCall("{ call peekOldestMessage(?) }");
                }
            }
            cst.setInt(1, queueId);
            if(clientId != 0) {
                cst.setInt(2, clientId);
            }
            ResultSet rs = cst.executeQuery();
            rs.next(); // if result set is empty we get an exception
            Message m = ModelFactory.createMessage(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getTimestamp(7), rs.getString(8));
            rs.close();
            cst.close();
            return m;
        } catch(SQLException e) {
            if(e.getSQLState().equals("V2003")) {
                throw new QueueDoesNotExistException(e);
            } else if (e.getSQLState().equals("V2004")) {
                throw new NoMessageInQueueException(e);
            } else if (e.getSQLState().equals("V2005")) {
                throw new NoMessageFromSenderException(e);
            }
            throw new MessagePeekingException(e);
        }
    }

}
