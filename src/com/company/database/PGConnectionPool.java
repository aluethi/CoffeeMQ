package com.company.database;

import com.company.config.Configuration;
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
public class PGConnectionPool {

    private static Logger LOGGER_ = Logger.getLogger(PGConnectionPool.class.getCanonicalName());
    private static PGConnectionPool INSTANCE_ = null;

    private PGPoolingDataSource source_;

    public static PGConnectionPool getInstance() {
        if(INSTANCE_ == null) {
            INSTANCE_ = new PGConnectionPool();
        }
        return INSTANCE_;
    }

    private PGConnectionPool() {
        source_ = new PGPoolingDataSource();
        source_.setServerName(Configuration.getProperty("db.server.name"));
        source_.setDatabaseName(Configuration.getProperty("db.database.name"));
        source_.setUser(Configuration.getProperty("db.user"));
        source_.setPassword(Configuration.getProperty("db.password"));
        source_.setMaxConnections(Integer.parseInt(Configuration.getProperty("db.pool.max")));
        source_.setInitialConnections(Integer.parseInt(Configuration.getProperty("db.pool.initial")));
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
