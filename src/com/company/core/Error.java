package com.company.core;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Error {

    public static int STATUS_OK = 0;
    public static int STATUS_ERROR = 1;

    public static int EC_CLIENT_CREATION_EXCEPTION = 0;
    public static int EC_CLIENT_DELETION_EXCEPTION = 1;

    private int status_;
    private int errorCode_;

    public static Error ok() {
        return new Error(STATUS_OK);
    }

    public static Error err(int errorCode) {
        return new Error(STATUS_ERROR, errorCode);
    }

    public Error(int status) {
        this(status, 0);
    }

    public Error(int status, int errorCode) {
        status_ = status;
        errorCode_ = errorCode;
    }

    public int getStatus() {
        return status_;
    }

    public int getErrorCode() {
        return errorCode_;
    }

}
