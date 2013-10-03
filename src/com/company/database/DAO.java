package com.company.database;

import com.company.model.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class DAO {
    private final IDatasource datasource_;

    public DAO(IDatasource datasource) {
        datasource_ = datasource;
    }

    private void init() {
        datasource_.connect();
    }

    public void createQueue(Queue q) {
        datasource_.createQueue(q);
    }
}
