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

    private ConcurrentLinkedQueue<String> logEntries_ = new ConcurrentLinkedQueue<String>();
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
        String entry;
        while(true) {
            while((entry = logEntries_.poll()) != null) {
                writer_.println(entry);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void log(String message) {
        logEntries_.add(message);
    }

}
