package com.company.test.database;

import com.company.config.Configuration;
import com.company.database.PGConnectionPool;
import com.company.database.QueueDao;
import com.company.exception.QueueDoesNotExistException;
import com.company.exception.QueueExistsException;
import com.company.model.ModelFactory;
import com.company.model.Queue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 08/11/13
 * Time: 09:15
 * To change this template use File | Settings | File Templates.
 */
public class QueueDaoTest {
    Connection connection_;
    QueueDao dao_;

    @Before
    public void setUp() throws Exception {
        Configuration.initConfig("var/config.prop");
        connection_ = PGConnectionPool.getInstance().getConnection();
        dao_ = new QueueDao(connection_);
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE queue");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        connection_.close();
    }

    @Test
    public void testCreateQueue() throws Exception {
        Queue q = ModelFactory.createQueue(10);
        dao_.createQueue(q);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM queue WHERE Id = ?");
        ps.setInt(1, 10);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Timestamp ts = rs.getTimestamp(2);
            assertEquals("Queue was not created correctly.", ts, q.getCreated());
        } else {
            assertTrue("Queue was not created.", false);
        }
    }

    @Test(expected=QueueExistsException.class)
    public void testCreateQueue_QueueExistsException() throws Exception {
        Queue q1 = ModelFactory.createQueue(20);
        Queue q2 = ModelFactory.createQueue(20);
        dao_.createQueue(q1);
        dao_.createQueue(q2);
    }

    @Test
    public void testReadQueue() throws Exception {
        Queue q1 = ModelFactory.createQueue(30);
        dao_.createQueue(q1);
        Queue q2 = dao_.readQueue(30);
        assertEquals("Retrieved queue not equal.", q1, q2);
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testReadQueue_QueueDoesNotExistException() throws Exception {
        dao_.readQueue(40);
    }

    @Test
    public void testDeleteQueue() throws Exception {
        Queue q = ModelFactory.createQueue(50);
        dao_.createQueue(q);
        dao_.deleteQueue(50);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM queue WHERE Id = ?");
        ps.setInt(1, 50);
        ResultSet rs = ps.executeQuery();
        assertTrue("Queue could not be deleted.", !rs.next());
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testDeleteQueue_QueueDoesNotExistException() throws Exception {
        Queue q = ModelFactory.createQueue(60);
        dao_.createQueue(q);
        dao_.deleteQueue(60);
        dao_.deleteQueue(60);
    }
}
