package com.company.exception;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 08/11/13
 * Time: 07:56
 * To change this template use File | Settings | File Templates.
 */
public class QueueExistsException extends Exception {
    public QueueExistsException(SQLException e) {
    }
}
