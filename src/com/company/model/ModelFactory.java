package com.company.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 03.10.13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class ModelFactory {

    public static Queue createQueue() {
        return createQueue(new Date());
    }

    public static Queue createQueue(Date created) {
        return new Queue(created);
    }

    public static Message createMessage(int sender, int receiver, int queue, int context, int priority, Date created, String message) {
        return new Message(sender, receiver, queue, context, priority, created, message);
    }
}
