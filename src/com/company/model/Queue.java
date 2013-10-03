package com.company.model;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Queue {

    private final int queueId_;
    private final Timestamp created_;

    public Queue(int queueId, Timestamp created) {
        queueId_ = queueId;
        created_ = created;
    }

    public int getQueueId() {
        return queueId_;
    }

    public Timestamp getCreatedValue() {
        return created_;
    }
}
