package com.company.test.database;

import com.company.config.Configuration;
import com.company.database.PGConnectionPool;
import com.company.management.PGDatasource;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;
import org.junit.*;

import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/8/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PGDatasourceTest {

    private static PGDatasource ds;
    private static Client s; //Sender
    private static Client r; //Receiver
    private static Client d;
    private static Queue q;
    private static Queue q1;
    private  static Message m;

    @BeforeClass
    public static void setUp() throws Exception {
        //Prepare needed objects
        Configuration.initConfig("var/config.prop");
        ds = new PGDatasource();
        s = ModelFactory.createClient(1);
        r = ModelFactory.createClient(2);
        d = ModelFactory.createClient(3);
        q = ModelFactory.createQueue(1);
        q1 = ModelFactory.createQueue(2);
        m = ModelFactory.createMessage(1, 2, 1, 1, 1, "Hallo");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        //Truncate all tables
        Connection con = PGConnectionPool.getInstance().getConnection();
        CallableStatement cst = con.prepareCall("TRUNCATE TABLE client, queue");
        cst.execute();
        cst.close();
        con.close();
    }

    @Test
    public void testCreateClient() throws Exception {
        ds.createClient(s);
        ds.createClient(r);
        ds.createClient(d);
    }

    @Test
    public void testDeleteClient() throws Exception {
        ds.deleteClient(d);
    }

    @Test
    public void testCreateQueue() throws Exception {
        ds.createQueue(q);
        ds.createQueue(q1);
    }

    @Test
    public void testDeleteQueue() throws Exception {
        ds.deleteQueue(q1);
    }

    @Test
    public void testEnqueueMessage() throws Exception {
        ds.enqueueMessage(m);
        System.out.println("id of enqueued message: '" + m.getId() + "'");
    }

    @Test
    public void testDequeueMessage() throws Exception {
        Message returnedMessage = ds.dequeueMessage(q, false);
        System.out.println("id of dequeued message: '" + returnedMessage.getId() + "'");

        Message returnedMessageHp = ds.dequeueMessage(q, true);
        System.out.println("id of dequeued message (hp): '" + returnedMessageHp.getId() + "'");

        Message returnedMessageFromSender = ds.dequeueMessage(q, s, false);
        System.out.println("id of dequeued message from s: '" + returnedMessageFromSender.getId() + "'");

        Message returnedMessageFromSenderHp = ds.dequeueMessage(q, s, true);
        System.out.println("id of dequeued message from s (hp): '" + returnedMessageFromSenderHp.getId() + "'");

        /*System.out.println("sender of dequeued message: '" + returnedMessage.getSender() + "'");
        System.out.println("receiver of dequeued message: '" + returnedMessage.getReceiver() + "'");
        System.out.println("queue of dequeued message: '" + returnedMessage.getQueue() + "'");
        System.out.println("context of dequeued message: '" + returnedMessage.getContext() + "'");
        System.out.println("priority of dequeued message: '" + returnedMessage.getPriority() + "'");
        System.out.println("created of dequeued message: '" + returnedMessage.getCreated().toString() + "'");
        System.out.println("message of dequeued message: '" + returnedMessage.getMessage() + "'");*/
    }

    @Test
    public void testPeekMessage() throws Exception {
        Message returnedMessage = ds.peekMessage(q, false);
        System.out.println("id of peeked message: '" + returnedMessage.getId() + "'");

        Message returnedMessageHp = ds.peekMessage(q, true);
        System.out.println("id of peeked message (hp): '" + returnedMessageHp.getId() + "'");

        Message returnedMessageFromSender = ds.peekMessage(q, s, false);
        System.out.println("id of peeked message from s: '" + returnedMessageFromSender.getId() + "'");

        Message returnedMessageFromSenderHp = ds.peekMessage(q, s, true);
        System.out.println("id of peeked message from s (hp): '" + returnedMessageFromSenderHp.getId() + "'");
    }

}
