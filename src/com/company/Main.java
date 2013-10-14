package com.company;

import com.company.config.Configuration;
import com.company.exception.QueueCreationException;
import com.company.network.Acceptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private Acceptor acceptor_;
    private ExecutorService executor_;

    public static void main(String[] args) throws QueueCreationException {
        String configFilePath = "var/config.prop";

        if(args.length > 1) {
            configFilePath = args[1];
        }

        Configuration.initConfig(configFilePath);

        new Main().start();
    }

    public Main() {
        init();
    }

    private void init() {
        executor_ = Executors.newFixedThreadPool(Integer.parseInt(Configuration.getProperty("mw.pool.max")));
        acceptor_ = new Acceptor(Configuration.getProperty("net.iface.ip"), Integer.parseInt(Configuration.getProperty("net.iface.port")), executor_);
    }

    public void start() {
        new Thread(acceptor_).start();
    }

}
