package com.company.exception;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 08/11/13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class NoMessageFromSenderException extends Exception {
    public NoMessageFromSenderException(SQLException e) {
    }
}
