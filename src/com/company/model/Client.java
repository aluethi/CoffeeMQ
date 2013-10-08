package com.company.model;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 08.10.13
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    private int id_;
    private Timestamp created_;

    public Client(int id, Timestamp created) {
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
}
