package com.company;

import com.company.config.Configuration;
import com.company.exception.QueueCreationException;
import com.company.logger.LoggerSingleton;
import com.company.network.Acceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private Acceptor acceptor_;
    private ExecutorService executor_;

    public static void main(String[] args) throws QueueCreationException {
        String banner = "----------------------------\n" +
                "| Welcome to CoffeeMQ v0.1 |\n" +
                "----------------------------\n";
        String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date(System.currentTimeMillis()));
        System.out.print(banner);
        String configFilePath = "var/config.prop";
        String logPath = "var/" + date + "-log.csv";

        if(args.length >= 1) {
            configFilePath = args[0];
        }

        Configuration.initConfig(configFilePath);
        Configuration.putProperty("log.perf.path", logPath);

        new Main().start();
    }

    public Main() {
        init();
    }

    private void init() {
        LoggerSingleton.initLogger(Configuration.getProperty("log.perf.path"));
        executor_ = Executors.newFixedThreadPool(Integer.parseInt(Configuration.getProperty("mw.pool.max")));
        acceptor_ = new Acceptor(Configuration.getProperty("net.iface.ip"), Integer.parseInt(Configuration.getProperty("net.iface.port")), executor_);
    }

    public void start() {
        new Thread(acceptor_).start();
    }

}
