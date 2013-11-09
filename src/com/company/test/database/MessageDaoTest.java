package com.company.test.database;

import com.company.config.Configuration;
import com.company.database.ClientDao;
import com.company.database.MessageDao;
import com.company.database.PGConnectionPool;
import com.company.database.QueueDao;
import com.company.exception.NoMessageFromSenderException;
import com.company.exception.NoMessageInQueueException;
import com.company.exception.QueueDoesNotExistException;
import com.company.exception.SenderDoesNotExistException;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 08/11/13
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public class MessageDaoTest {
    Connection connection_;
    MessageDao dao_;
    QueueDao queueDao_;
    ClientDao clientDao_;

    private static int CLIENT_ID = 123;
    private static int QUEUE_ID = 100;

    @Before
    public void setUp() throws Exception {
        Configuration.initConfig("var/config.prop");
        connection_ = PGConnectionPool.getInstance().getConnection();
        dao_ = new MessageDao(connection_);
        queueDao_ = new QueueDao(connection_);
        clientDao_ = new ClientDao(connection_);
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE message; TRUNCATE TABLE queue");
        ps.execute();
        Queue q = ModelFactory.createQueue(QUEUE_ID);
        queueDao_.createQueue(q);
        Client c = ModelFactory.createClient(CLIENT_ID);
        clientDao_.createClient(c);
        c = ModelFactory.createClient(CLIENT_ID-1);
        clientDao_.createClient(c);
    }

    @After
    public void tearDown() throws Exception {
        queueDao_.deleteQueue(QUEUE_ID);
        clientDao_.deleteClient(CLIENT_ID);
        clientDao_.deleteClient(CLIENT_ID-1);
        connection_.close();
    }

    @Test
    public void testEnqueueMessage() throws Exception {
        Message m = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM message WHERE queue = ?");
        ps.setInt(1, QUEUE_ID);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Timestamp ts = rs.getTimestamp(7);
            assertEquals("Enqueue message does not work.", ts, m.getCreated());
        } else {
            assert(false);
        }
    }

    @Test(expected=SenderDoesNotExistException.class)
    public void testEnqueueMessage_SenderDoesNotExistException() throws Exception {
        Message m = ModelFactory.createMessage(CLIENT_ID+1, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m);
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testEnqueueMessage_QueueDoesNotExistException() throws Exception {
        Message m = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID+1, 0, 0, "test");
        dao_.enqueueMessage(m);
    }

    @Test
    public void testDequeueMessage_QueueId() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m1);
        Thread.sleep(100);
        Message m2 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test2");
        dao_.enqueueMessage(m2);
        Message m3 = dao_.dequeueMessage(QUEUE_ID);
        assertEquals("Did not retrieve correct message.", m1.getMessage(), m3.getMessage());
    }

    @Test
    public void testDequeueMessage_QueueIdHighestPrio() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 10, "test");
        dao_.enqueueMessage(m1);
        Message m2 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m2);
        Thread.sleep(100);
        Message m3 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test2");
        dao_.enqueueMessage(m3);
        Message m4 = dao_.dequeueMessage(QUEUE_ID, true);
        assertEquals("Did not retrieve correct message.", m1.getMessage(), m4.getMessage());
    }

    @Test
    public void testDequeueMessage_QueueIdClientId() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m1);
        Thread.sleep(100);
        Message m2 = ModelFactory.createMessage(CLIENT_ID-1, 20, QUEUE_ID, 0, 0, "test2");
        dao_.enqueueMessage(m2);
        Message m3 = dao_.dequeueMessage(QUEUE_ID, CLIENT_ID-1, false);
        assertEquals("Did not retrieve correct message.", m2.getMessage(), m3.getMessage());
    }

    @Test
    public void testDequeueMessage_QueueIdClientIdHighestPrio() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 10, "test");
        dao_.enqueueMessage(m1);
        Message m2 = ModelFactory.createMessage(CLIENT_ID-1, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m2);
        Thread.sleep(100);
        Message m3 = ModelFactory.createMessage(CLIENT_ID-1, 20, QUEUE_ID, 0, 5, "test2");
        dao_.enqueueMessage(m3);
        Message m4 = dao_.dequeueMessage(QUEUE_ID, CLIENT_ID-1, true);
        assertEquals("Did not retrieve correct message.", m2.getMessage(), m4.getMessage());
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testDequeueMessage_QueueDoesNotExistException() throws Exception {
        dao_.dequeueMessage(QUEUE_ID+1);
    }

    @Test(expected=NoMessageInQueueException.class)
    public void testDequeueMessage_NoMessageInQueueException() throws Exception {
        Queue q = ModelFactory.createQueue(101);
        queueDao_.createQueue(q);
        dao_.dequeueMessage(101);
    }

    @Test(expected=NoMessageFromSenderException.class)
    public void testDequeueMessage_NoMessageFromSenderException() throws Exception {
        Queue q = ModelFactory.createQueue(101);
        queueDao_.createQueue(q);
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, 101, 0, 10, "test");
        dao_.enqueueMessage(m1);
        dao_.dequeueMessage(101, CLIENT_ID+1, false);
    }

    @Test
    public void testPeekMessage_QueueId() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m1);
        Thread.sleep(100);
        Message m2 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test2");
        dao_.enqueueMessage(m2);
        Message m3 = dao_.peekMessage(QUEUE_ID);
        assertEquals("Did not retrieve correct message.", m1.getMessage(), m3.getMessage());

        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM message WHERE Id = ?");
        ps.setInt(1, m3.getId());
        ResultSet rs = ps.executeQuery();
        assertTrue("Result set after peek empty.", rs.next());
    }

    @Test
    public void testPeekMessage_QueueIdHighestPrio() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 10, "test");
        dao_.enqueueMessage(m1);
        Message m2 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m2);
        Thread.sleep(100);
        Message m3 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test2");
        dao_.enqueueMessage(m3);
        Message m4 = dao_.peekMessage(QUEUE_ID, true);
        assertEquals("Did not retrieve correct message.", m1.getMessage(), m4.getMessage());

        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM message WHERE Id = ?");
        ps.setInt(1, m4.getId());
        ResultSet rs = ps.executeQuery();
        assertTrue("Result set after peek empty.", rs.next());
    }

    @Test
    public void testPeekMessage_QueueIdClientId() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m1);
        Thread.sleep(100);
        Message m2 = ModelFactory.createMessage(CLIENT_ID-1, 20, QUEUE_ID, 0, 0, "test2");
        dao_.enqueueMessage(m2);
        Message m3 = dao_.peekMessage(QUEUE_ID, CLIENT_ID-1, false);
        assertEquals("Did not retrieve correct message.", m2.getMessage(), m3.getMessage());

        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM message WHERE Id = ?");
        ps.setInt(1, m3.getId());
        ResultSet rs = ps.executeQuery();
        assertTrue("Result set after peek empty.", rs.next());
    }

    @Test
    public void testPeekMessage_QueueIdClientIdHighestPrio() throws Exception {
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, QUEUE_ID, 0, 10, "test");
        dao_.enqueueMessage(m1);
        Message m2 = ModelFactory.createMessage(CLIENT_ID-1, 20, QUEUE_ID, 0, 0, "test");
        dao_.enqueueMessage(m2);
        Thread.sleep(100);
        Message m3 = ModelFactory.createMessage(CLIENT_ID-1, 20, QUEUE_ID, 0, 5, "test2");
        dao_.enqueueMessage(m3);
        Message m4 = dao_.peekMessage(QUEUE_ID, CLIENT_ID - 1, true);
        assertEquals("Did not retrieve correct message.", m2.getMessage(), m4.getMessage());

        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM message WHERE Id = ?");
        ps.setInt(1, m4.getId());
        ResultSet rs = ps.executeQuery();
        assertTrue("Result set after peek empty.", rs.next());
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testPeekMessage_QueueDoesNotExistException() throws Exception {
        dao_.peekMessage(QUEUE_ID + 1);
    }

    @Test(expected=NoMessageInQueueException.class)
    public void testPeekMessage_NoMessageInQueueException() throws Exception {
        Queue q = ModelFactory.createQueue(101);
        queueDao_.createQueue(q);
        dao_.peekMessage(101);
    }

    @Test(expected=NoMessageFromSenderException.class)
    public void testPeekMessage_NoMessageFromSenderException() throws Exception {
        Queue q = ModelFactory.createQueue(101);
        queueDao_.createQueue(q);
        Message m1 = ModelFactory.createMessage(CLIENT_ID, 20, 101, 0, 10, "test");
        dao_.enqueueMessage(m1);
        dao_.peekMessage(101, CLIENT_ID + 1, false);
    }
}
