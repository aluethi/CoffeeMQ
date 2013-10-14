package com.company;

import com.company.config.Configuration;
import com.company.exception.QueueCreationException;
import com.company.network.Acceptor;

import java.util.concurrent.Executor;

public class Main {

    private Acceptor acceptor_;
    private Executor executor_;

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
        acceptor_ = new Acceptor(Configuration.getProperty("net.iface.ip"), Integer.parseInt(Configuration.getProperty("net.iface.port")));
    }

    public void start() {
        new Thread(acceptor_).start();
    }

}
