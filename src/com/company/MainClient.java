package com.company;

import com.company.client.Message;
import com.company.client.MessageService;
import com.company.client.Queue;
import com.company.exception.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainClient {
    public static void main(String[] args) {
        MessageService msgService = new MessageService("localhost", 5555);

        try {
            msgService.register("Test");

            Queue q1 = msgService.createQueue("Queue1");
            Message m1 = new Message("Test".hashCode(), 1, 1, "Hallo Welt!");
            Message m2 = new Message("Test2".hashCode(), 1, 1, "Hallo Welt nochmal!");
            q1.put(m1);
            q1.put(m2);
            q1.get();

            msgService.deregister();
        } catch (RegisterFailureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DeregisterFailureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NonExistentQueueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MsgInsertionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MsgRetrievalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
