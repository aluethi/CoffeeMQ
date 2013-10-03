package com.company;

import com.company.model.Message;
import com.company.model.Queue;

import com.company.database.PGDatasource;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to CoffeeMQ! We're happy to serve. :)\n");

        PGDatasource test = new PGDatasource();
        test.connect();
        Queue testQueue1 = test.createQueue();
        Queue testQueue2 = test.createQueue();
        //System.out.println(testQueue.getCreatedValue().toString());
        test.deleteQueue(testQueue1);
        test.disconnect();

    }
}
