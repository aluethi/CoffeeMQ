package com.company.model;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Queue {

    private int id_;
    private Date created_;

    public Queue(Date created) {
        created_ = created;
    }

    public int getId() {
        return id_;
    }

    public Date getCreated() {
        return created_;
    }
}
