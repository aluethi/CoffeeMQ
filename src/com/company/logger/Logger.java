package com.company.logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 09/11/13
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class Logger implements Runnable {

    private ConcurrentLinkedQueue<String> logEntries_;
    private PrintWriter writer_;

    public Logger(String fileName) {
        try {
            writer_ = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run() {
        while(true) {
            int size = logEntries_.size();
            int i = 0;
            while(size > i) {
                writer_.println(logEntries_.poll());
            }
            Thread.sleep();
        }
    }

    public void log(String message) {

    }

}
