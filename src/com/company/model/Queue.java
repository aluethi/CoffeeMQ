package com.company.model;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Queue {

    private int id_;
    private Timestamp created_;

    public Queue(Timestamp created) {
        created_ = created;
    }

    public int getId() {
        return id_;
    }

    public Timestamp getCreated() {
        return created_;
    }
}
