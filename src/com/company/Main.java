package com.company;

import com.company.config.Configuration;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;

import java.util.Date;

import com.company.database.PGDatasource;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to CoffeeMQ! We're happy to sever your server. ;-)\n");

        Configuration.getProperty("");


        Queue q1 = ModelFactory.createQueue();
        Queue q2 = ModelFactory.createQueue();
        PGDatasource test = new PGDatasource();
        test.connect();
        test.createQueue(q1);
        test.createQueue(q2);
        test.deleteQueue(q1);
        //System.out.println(testQueue.getCreatedValue().toString());
        //test.deleteQueue(testQueue1);
        test.disconnect();
    }
}
