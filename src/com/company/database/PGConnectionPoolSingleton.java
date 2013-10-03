package com.company.database;

import org.postgresql.ds.PGPoolingDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PGConnectionPoolSingleton {

    private static Logger LOGGER_ = Logger.getLogger(PGConnectionPoolSingleton.class.getCanonicalName());
    private static PGConnectionPoolSingleton INSTANCE_ = null;

    private PGPoolingDataSource source_;

    public static PGConnectionPoolSingleton getInstance() {
        if(INSTANCE_ == null) {
            INSTANCE_ = new PGConnectionPoolSingleton();
        }
        return INSTANCE_;
    }

    private PGConnectionPoolSingleton() {
        source_ = new PGPoolingDataSource();
        source_.setServerName("localhost");
        source_.setDatabaseName("coffeemq");
        source_.setUser("coffeemquser");
        source_.setPassword("coffeemqrules");
        source_.setMaxConnections(10);
        source_.setInitialConnections(10);
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = source_.getConnection();
        } catch (SQLException e) {
            LOGGER_.log(Level.SEVERE, "Could not retrieve connection from the connection pool.");
            throw new RuntimeException(e);
        }
        return con;
    }

}
