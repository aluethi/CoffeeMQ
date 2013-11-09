package com.company.testframework;

import com.company.client.Message;
import com.company.client.MessageService;
import com.company.client.Queue;
import com.company.exception.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/18/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleProducer {
   public static void main(String[] args) {

       String host = "localhost";
       int port = 5555;
       int msgCount = 0;

       MessageService msgService = new MessageService(host, port);
       MessageService msgService2 = new MessageService(host, port);

       try {
           msgService.register("SimpleProducer");
           msgService2.register("SimpleProducer2");
           msgService.createQueue("SimpleQueue");
           Queue q = msgService.getQueue("SimpleQueue");
           while(true) {
               Message m = new Message(0,0,0,"Message: " + msgCount++);
               q.put(m);
           }
       } catch (RegisterFailureException e) {
           System.out.println("Couldn't register at the message queue.");
       } catch (SenderDoesNotExistException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (QueueDoesNotExistException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (MessageEnqueueingException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (QueueReadException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (QueueCreationException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (QueueExistsException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (ClientExistsException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       }

   }
}
