package com.company.logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 11/11/13
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
public class LoggerSingleton {

    private static Logger INSTANCE_ = null;

    public static void initLogger(String logPath) {
        INSTANCE_ = new Logger(logPath);
        new Thread(INSTANCE_).start();
    }

    public static Logger getLogger() {
        return INSTANCE_;
    }

}
