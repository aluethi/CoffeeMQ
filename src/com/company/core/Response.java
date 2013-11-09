package com.company.core;

import com.company.model.Message;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Response {

    public static final int MSG_REGISTER = 1;
    public static final int MSG_DEREGISTER = 2;
    public static final int MSG_CREATE_QUEUE = 3;
    public static final int MSG_GET_QUEUE = 4;
    public static final int MSG_DELETE_QUEUE = 5;
    public static final int MSG_PUT_INTO_QUEUE = 6;
    public static final int MSG_GET = 7;
    public static final int MSG_PEEK = 8;

    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;

    public static final int ERR_CLIENT_CREATION_EXCEPTION = 1;
    public static final int ERR_CLIENT_EXISTS_EXCEPTION = 2;
    public static final int ERR_CLIENT_DELETION_EXCEPTION = 3;
    public static final int ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION = 4;

    public static final int ERR_QUEUE_CREATION_EXCEPTION = 41;
    public static final int ERR_QUEUE_EXISTS_EXCEPTION = 42;
    public static final int ERR_QUEUE_READ_EXCEPTION = 43;
    public static final int ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION = 44;
    public static final int ERR_QUEUE_DELETION_EXCEPTION = 45;

    public static final int ERR_MESSAGE_ENQUEUEING_EXCEPTION = 81;
    public static final int ERR_SENDER_DOES_NOT_EXIST_EXCEPTION = 82;
    public static final int ERR_MESSAGE_DEQUEUEING_EXCEPTION = 83;
    public static final int ERR_NO_MESSAGE_IN_QUEUE_EXCEPTION = 84;
    public static final int ERR_NO_MESSAGE_FROM_SENDER_EXCEPTION = 85;
    public static final int ERR_MESSAGE_PEEKING_EXCEPTION = 86;

    private final int status_;
    private final int errorCode_;
    private final Message m_;

    public static Response ok() {
        return new Response(STATUS_OK);
    }

    public static Response ok(Message m) {
        return new Response(STATUS_OK, m);
    }

    public static Response err(int errorCode) {
        return new Response(STATUS_ERROR, errorCode);
    }

    public Response(int status) {
        this(status, 0, null);
    }

    public Response(int status, Message m) {
        this(status, 0, m);
    }

    public Response(int status, int errorCode) {
        this(status, errorCode, null);
    }

    public Response(int status, int errorCode, Message m) {
        status_ = status;
        errorCode_ = errorCode;
        m_ = m;
    }

    public void serialize(ByteBuffer buffer) {
        buffer.clear();
        buffer.putInt(status_);
        if(status_ != STATUS_OK) {
            buffer.putInt(errorCode_);
            return;
        } else if(m_ != null) {
            buffer.putInt(m_.getSender());
            buffer.putInt(m_.getReceiver());
            buffer.putInt(m_.getContext());
            buffer.putInt(m_.getPriority());
            buffer.putInt(m_.getMessage().getBytes().length);
            buffer.put(m_.getMessage().getBytes());
        }
    }

}
