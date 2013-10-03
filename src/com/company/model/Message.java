package com.company.model;


/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Message {

    private final int messageId_;
    private final int queue_;
    private final String message_;

    public Message(int messageId, int queue, String message) {
        messageId_ = messageId;
        queue_ = queue;
        message_ = message;
    }

    public int getMessageId() {
        return messageId_;
    }

    public int getQueue() {
        return queue_;
    }

    public String getMessage() {
        return message_;
    }

}
