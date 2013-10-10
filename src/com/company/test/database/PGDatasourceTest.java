package com.company.test.database;

import com.company.config.Configuration;
import com.company.database.PGConnectionPool;
import com.company.database.PGDatasource;
import com.company.exception.ClientCreationException;
import com.company.exception.QueueCreationException;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;
import org.junit.*;
import org.junit.runners.model.Statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

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
    private static Queue q;
    private  static Message m;

    @BeforeClass
    public static void setUp() throws Exception {
        //Prepare needed objects
        Configuration.initConfig("var/config.prop");
        ds = new PGDatasource();
        s = ModelFactory.createClient(1);
        r = ModelFactory.createClient(2);
        q = ModelFactory.createQueue(1);
        m = ModelFactory.createMessage(1, 2, 1, 1, 1, "Hallo");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        //Truncate all tables
        Connection con = PGConnectionPool.getInstance().getConnection();
        CallableStatement cst = con.prepareCall("TRUNCATE TABLE client, message, queue");
        cst.execute();
        cst.close();
        con.close();
    }

    @Test
    public void testCreateClient() throws Exception {
        ds.createClient(s);
        ds.createClient(r);
    }

    @Test
    public void testDeleteClient() throws Exception {

    }

    @Test
    public void testCreateQueue() throws Exception {
        ds.createQueue(q);
    }

    @Test
    public void testDeleteQueue() throws Exception {

    }

    @Test
    public void testEnqueueMessage() throws Exception {
        ds.enqueueMessage(m);
        System.out.println("id of enqueued message: '" + m.getId() + "'");
    }

    @Test
    public void testDequeueMessage() throws Exception {
        ds.dequeueMessage(q, false);
    }

    @Test
    public void testPeekMessage() throws Exception {

    }

}
