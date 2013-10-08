package com.company;

import com.company.config.Configuration;
import com.company.database.PGConnectionPool;
import com.company.exception.QueueCreationException;
import com.company.model.ModelFactory;
import com.company.model.Queue;

import com.company.database.PGDatasource;
import com.company.network.Acceptor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    private Acceptor acceptor_;
    private Executor executor_;

    public static void main(String[] args) throws QueueCreationException {
        String configFilePath = "config.prop";

        if(args.length > 1) {
            configFilePath = args[1];
        }

        Configuration.initConfig(configFilePath);

        new Main().start();
    }

    public Main() {

    }

    private void init() {
        executor_ = Executors.newFixedThreadPool(Integer.parseInt(Configuration.getProperty("mw.pool.max")));
        acceptor_ = new Acceptor(Configuration.getProperty("net.iface.ip"), Integer.parseInt(Configuration.getProperty("net.iface.port")), executor_);
    }

    public void start() {
        new Thread(acceptor_).start();
    }

}
