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

    public Queue(int id, Timestamp created) {
        id_ = id;
        created_ = created;
    }

    public void setId(int id) {
        id_ = id;
    }

    public int getId() {
        return id_;
    }

    public Timestamp getCreated() {
        return created_;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Queue)) return false;
        Queue that = (Queue) other;
        return (that.getCreated().equals(created_) && that.getId() == id_);
    }
}
