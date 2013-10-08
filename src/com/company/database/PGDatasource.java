package com.company.database;

import com.company.exception.ClientCreationException;
import com.company.exception.QueueCreationException;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.Queue;

import java.sql.*;
import java.util.Date;
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

    private Connection con_;
    private CallableStatement cst_;
    private Statement st_;
    private ResultSet rs_;

    @Override
    public void createClient(Client c) throws ClientCreationException {
        //Insert a new queue into table 'Queue'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call createclient(?, ?) }");
            cst.setInt(1, c.getId());
            cst.setTimestamp(2, c.getCreated());
            cst.execute();
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
    public void deleteClient(Client c) {
        //Delete queue from table 'Queue'
        try {
            cst_ = con_.prepareCall("{ call deleteclient(?) }");
            cst_.setInt(1, c.getId());
            cst_.execute();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(PGDatasource.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void createQueue(Queue q) throws QueueCreationException {
        //Insert a new queue into table 'Queue'
        Connection con = PGConnectionPool.getInstance().getConnection();
        try {
            CallableStatement cst = con.prepareCall("{ call createqueue(?, ?) }");
            cst.setInt(1, q.getId());
            cst.setTimestamp(2, q.getCreated());
            cst.execute();
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
    public void deleteQueue(Queue q) {
        //Delete queue from table 'Queue'
        try {
            cst_ = con_.prepareCall("{ call deletequeue(?) }");
            cst_.setInt(1, q.getId());
            cst_.execute();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(PGDatasource.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void putMessage(Queue q, Message m) {
        //Insert a new message into table 'Message' with attribute 'Queue' set to the id of the respective queue
        try {
            Date created = new Date();
            st_.executeUpdate("INSERT INTO message(queue, created, message) VALUES(" + q.getId() + ", '" + created.toString() + "', " + m.getMessage() + ")");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(PGDatasource.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void connect() {
        //Connect to db 'coffeemq' with user 'coffeemquser'

        String url = "jdbc:postgresql://localhost/coffeemq";
        String user = "coffeemquser";
        String password = "coffeemqrules";

        try {
            con_ = DriverManager.getConnection(url, user, password);
            st_ = con_.createStatement();

            /*rs_ = st_.executeQuery("SELECT VERSION()");

            if (rs_.next()) {
                System.out.println(rs_.getString(1));
            }*/

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(PGDatasource.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (rs_ != null) {
                rs_.close();
            }
            if (st_ != null) {
                st_.close();
            }
            if (cst_ != null) {
                cst_.close();
            }
            if (con_ != null) {
                con_.close();
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(PGDatasource.class.getName());
            lgr.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
}
