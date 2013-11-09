package com.company.exception;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 07/11/13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
public class ClientExistsException extends Exception {

    SQLException e_;

    public ClientExistsException(SQLException e) {
        e_ = e;
    }

    public SQLException getSQLException() {
        return e_;
    }
}
