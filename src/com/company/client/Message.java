package com.company.client;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 14.10.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class Message {

    private int sender_;
    private int receiver_;
    private int context_;
    private int priority_;
    private String message_;

    public Message(int receiver, int context, int priority, String message) {
        receiver_ = receiver;
        context_ = context;
        priority_ = priority;
        message_ = message;
    }

    public int getSender() {
        return sender_;
    }

    public int getReceiver() {
        return receiver_;
    }

    public int getContext() {
        return context_;
    }

    public int getPriority() {
        return priority_;
    }

    public String getMessage() {
        return message_;
    }

    public void setSender(int sender) {
        sender_ = sender;
    }

    public void setReceiver(int receiver) {
        receiver_ = receiver;
    }

    public void setContext(int context) {
        context_ = context;
    }

    public void setPriority(int priority) {
        priority_ = priority;
    }

    public void setMessage(String message) {
        message_ = message;
    }
}
