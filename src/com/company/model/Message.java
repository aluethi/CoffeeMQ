package com.company.model;


import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Message {

    private int id_;
    private int sender_;
    private int receiver_;
    private int queue_;
    private int context_;
    private int priority_;
    private Date created_;
    private String message_;

    public Message(int sender, int receiver, int queue, int context, int priority, Date created, String message) {
        sender_ = sender;
        receiver_ = receiver;
        queue_ = queue;
        context_ = context;
        priority_ = priority;
        created_ = created;
        message_ = message;
    }

    public int getId() {
        return id_;
    }

    public int getSender() {
        return sender_;
    }

    public int getReceiver() {
        return receiver_;
    }

    public int getQueue() {
        return queue_;
    }

    public int getContext() {
        return context_;
    }

    public int getPriority() {
        return priority_;
    }

    public Date getCreated() {
        return created_;
    }

    public String getMessage() {
        return message_;
    }

}
