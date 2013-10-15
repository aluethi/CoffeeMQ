package com.company.client;

import com.company.core.MQProtocol;
import com.company.exception.DeregisterFailureException;
import com.company.exception.RegisterFailureException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 14.10.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class MessageServiceImpl {

    private static Logger LOGGER_ = Logger.getLogger(MessageServiceImpl.class.getCanonicalName());

    private Socket socket_;
    private DataInputStream in_;
    private DataOutputStream out_;

    public MessageServiceImpl(String host, int port) {
        init(host, port);
    }

    public void init(String host, int port) {
        try {
            socket_ = new Socket(host, port);
            in_ = new DataInputStream(socket_.getInputStream());
            out_ = new DataOutputStream(socket_.getOutputStream());
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open socket. Stopping.");
            throw new RuntimeException(e);
        }
    }

    public void register(String clientId) throws RegisterFailureException {
        register(clientId.hashCode());
    }

    public void register(int clientId) throws RegisterFailureException {
        try {
            out_.write(MQProtocol.MSG_REGISTER);
            out_.write(clientId);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                throw new RegisterFailureException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void deregister() throws DeregisterFailureException {
        try {
            out_.write(MQProtocol.MSG_DEREGISTER);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != MQProtocol.STATUS_OK) {
                throw new DeregisterFailureException();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Queue getQueue(String name) {
        return null;
    }

    public void deleteQueue(String name) {

    }

    public void put(List<Queue> queues, Message msg) {

    }

    public List<Queue> getWaitingQueues() {
        return null;
    }

}
