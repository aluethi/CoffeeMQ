package com.company.model;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 03.10.13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class ModelFactory {

    public static Client createClient(int id) {
        return createClient(id, new Timestamp(System.currentTimeMillis()));
    }

    public static Client createClient(int id, Timestamp created) {
        return new Client(id, created);
    }

    public static Queue createQueue(int id) {
        return createQueue(id, new Timestamp(System.currentTimeMillis()));
    }

    public static Queue createQueue(int id, Timestamp created) {
        return new Queue(id, created);
    }

    public static Message createMessage(int id, int sender, int receiver, int queue, int context, int priority, Timestamp created, String message) {
        return new Message(id, sender, receiver, queue, context, priority, created, message);
    }

    public static Message createMessage(int sender, int receiver, int queue, int context, int priority, String message) {
        return createMessage(sender, receiver, queue, context, priority, new Timestamp(System.currentTimeMillis()), message);
    }

    public static Message createMessage(int sender, int receiver, int queue, int context, int priority, Timestamp created, String message) {
        return new Message(sender, receiver, queue, context, priority, created, message);
    }
}
