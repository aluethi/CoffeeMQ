package com.company;

import com.company.client.MessageServiceImpl;
import com.company.exception.DeregisterFailureException;
import com.company.exception.RegisterFailureException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainClient {
    public static void main(String[] args) {
        MessageServiceImpl msgService = new MessageServiceImpl("localhost", 5555);

        try {
            msgService.register("Test");
            msgService.deregister();
        } catch (RegisterFailureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DeregisterFailureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
