package com.company.database;

import com.company.exception.*;
import com.company.model.ModelFactory;
import com.company.model.Queue;

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
public class QueueDao {
    private Connection con_;

    public QueueDao(Connection con) {
        con_ = con;
    }

    public void createQueue(Queue q) throws QueueExistsException, QueueCreationException {
        try {
            CallableStatement cst = con_.prepareCall("{ call createQueue(?,?) }");
            cst.setInt(1, q.getId());
            cst.setTimestamp(2, q.getCreated());
            cst.execute();
            cst.close();
        } catch(SQLException e) {
            if(e.getSQLState().equals("23505")) { // DUPLICATE KEY
                throw new QueueExistsException(e);
            }
            throw new QueueCreationException(e);
        }
    }

    public Queue readQueue(int id) throws QueueReadException, QueueDoesNotExistException {
        try {
            CallableStatement cst = con_.prepareCall("{ call getQueue(?) }");
            cst.setInt(1, id);
            ResultSet rs = cst.executeQuery();
            rs.next(); // existence of record is checked in stored procedure
            Queue q = ModelFactory.createQueue(rs.getInt(1), rs.getTimestamp(2));
            rs.close();
            cst.close();
            return q;
        } catch(SQLException e) {
            if(e.getSQLState().equals("V2001")) { // CUSTOM: ID DOES NOT EXIST
                throw new QueueDoesNotExistException();
            }
            throw new QueueReadException(e);
        }
    }

    public void deleteQueue(int id) throws QueueDoesNotExistException, QueueDeletionException {
        try {
            CallableStatement cst = con_.prepareCall("{ call deleteQueue(?) }");
            cst.setInt(1, id);
            cst.execute();
            cst.close();
        } catch(SQLException e) {
            if(e.getSQLState().equals("V2001")) { // CUSTOM: ID DOES NOT EXIST
                throw new QueueDoesNotExistException(e);
            }
            throw new QueueDeletionException(e);
        }
    }
}
