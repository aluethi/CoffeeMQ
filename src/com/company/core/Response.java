package com.company.core;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Response {

    public static int STATUS_OK = 0;
    public static int STATUS_ERROR = 1;

    public static int EC_CLIENT_CREATION_EXCEPTION = 0;
    public static int EC_CLIENT_DELETION_EXCEPTION = 1;
    public static int EC_QUEUE_CREATION_EXCEPTION = 2;
    public static int EC_QUEUE_DELETION_EXCEPTION = 3;
    public static int EC_PUT_EXCEPTION = 4;
    public static int EC_GET_EXCEPTION = 5;

    private int status_ = 0;
    private int errorCode_ = 0;
    private Object payload_;

    public static Response ok() {
        return new Response(STATUS_OK);
    }

    public static Response err(int errorCode) {
        return new Response(STATUS_ERROR, errorCode);
    }

    public Response(int status) {
        this(status, 0);
    }

    public Response(int status, int errorCode) {
        status_ = status;
        errorCode_ = errorCode;
    }

    public int getStatus() {
        return status_;
    }

    public int getErrorCode() {
        return errorCode_;
    }

    public void serialize(ByteBuffer buffer) {
        buffer.putInt(status_);
        buffer.putInt(errorCode_);
    }

    public void deserialize(ByteBuffer buffer) {
        status_ = buffer.getInt();
    }

}
