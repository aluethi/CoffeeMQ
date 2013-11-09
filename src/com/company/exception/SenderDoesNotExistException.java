package com.company.exception;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 08/11/13
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class SenderDoesNotExistException extends Exception {
    public SenderDoesNotExistException(SQLException e) {
    }
}
