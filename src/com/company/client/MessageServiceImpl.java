package com.company.client;

import com.company.core.Response;
import com.company.exception.*;

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

    private String host_;
    private int port_;

    private int clientId_;

    public MessageServiceImpl(String host, int port) {
        host_ = host;
        port_ = port;
        init(host_, port_);
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

    private void tearDown() throws IOException {
        in_.close();
        out_.close();
        socket_.close();
    }

    public void register(String clientId) throws RegisterFailureException, ClientExistsException {
        register(clientId.hashCode());
    }

    public void register(int clientId) throws RegisterFailureException, ClientExistsException {
        clientId_ = clientId;
        try {
            out_.writeInt(8); //Size
            out_.writeInt(Response.MSG_REGISTER);
            out_.writeInt(clientId);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_CLIENT_CREATION_EXCEPTION) {
                    throw new RegisterFailureException();
                } else if (errorCode == Response.ERR_CLIENT_EXISTS_EXCEPTION) {
                    throw new ClientExistsException();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deregister() throws DeregisterFailureException, ClientDoesNotExistException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(Response.MSG_DEREGISTER);
            out_.writeInt(clientId_);
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_CLIENT_DELETION_EXCEPTION) {
                    throw new DeregisterFailureException();
                } else if(errorCode == Response.ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION) {
                    throw new ClientDoesNotExistException();
                }
            }
            tearDown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Queue createQueue(String queueId) throws QueueExistsException, QueueCreationException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(Response.MSG_CREATE_QUEUE);
            out_.writeInt(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_QUEUE_CREATION_EXCEPTION) {
                    throw new QueueCreationException();
                } else if(errorCode == Response.ERR_QUEUE_EXISTS_EXCEPTION) {
                    throw new QueueExistsException();
                }
            }
            return new Queue(this, queueId.hashCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Queue getQueue(String queueId) throws QueueReadException, QueueDoesNotExistException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(Response.MSG_GET_QUEUE);
            out_.writeInt(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_QUEUE_READ_EXCEPTION) {
                    throw new QueueReadException();
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException();
                }
            }
            return new Queue(this, queueId.hashCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteQueue(String queueId) throws QueueDeletionException, QueueDoesNotExistException {
        try {
            out_.writeInt(8); //Size
            out_.writeInt(Response.MSG_DELETE_QUEUE);
            out_.writeInt(queueId.hashCode());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_QUEUE_DELETION_EXCEPTION) {
                    throw new QueueDeletionException();
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(List<Queue> queues, Message msg) {
        //TODO
    }

    public List<Queue> getWaitingQueues() {
        //TODO
        return null;
    }

    public void put(int queueId, Message msg) throws MessageEnqueueingException, SenderDoesNotExistException, QueueDoesNotExistException {
        try {
            out_.writeInt(28 + msg.getMessage().getBytes().length); //Size
            out_.writeInt(Response.MSG_PUT_INTO_QUEUE);
            out_.writeInt(queueId); //Queue
            out_.writeInt(clientId_); //Sender
            out_.writeInt(msg.getReceiver()); //Receiver
            out_.writeInt(msg.getContext());
            out_.writeInt(msg.getPriority());
            out_.writeInt(msg.getMessage().getBytes().length);
            out_.write(msg.getMessage().getBytes());
            out_.flush();

            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_MESSAGE_ENQUEUEING_EXCEPTION) {
                    throw new MessageEnqueueingException();
                } else if(errorCode == Response.ERR_SENDER_DOES_NOT_EXIST_EXCEPTION) {
                    throw new SenderDoesNotExistException();
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Gets/peeks message from queue
    public Message get(int queueId, int senderId, boolean highestPriority) throws MessageDequeueingException, NoMessageInQueueException, QueueDoesNotExistException, NoMessageFromSenderException {
        int prio = highestPriority ? 1 : 0;

        try {
            out_.writeInt(16); //Size
            out_.writeInt(Response.MSG_GET);
            out_.writeInt(senderId);
            out_.writeInt(prio);
            out_.writeInt(queueId);
            out_.flush();

            //Read data
            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_MESSAGE_DEQUEUEING_EXCEPTION) {
                    throw new MessageDequeueingException();
                } else if(errorCode == Response.ERR_NO_MESSAGE_IN_QUEUE_EXCEPTION) {
                    throw new NoMessageInQueueException();
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException();
                } else if(errorCode == Response.ERR_NO_MESSAGE_FROM_SENDER_EXCEPTION) {
                    throw new NoMessageFromSenderException();
                }
            }
            int sender = in_.readInt();
            int receiver = in_.readInt();
            int context = in_.readInt();
            int priority = in_.readInt();
            int msgLength = in_.readInt();
            byte[] message = new byte[msgLength];
            in_.read(message, 0, msgLength);

           //Create object
            Message msg = new Message(receiver, context, priority, new String(message));
            msg.setSender(sender);
            return msg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Message peek(int queueId, int senderId, boolean highestPriority) throws NoMessageInQueueException, NoMessageFromSenderException, MessagePeekingException, QueueDoesNotExistException {
        int prio = highestPriority ? 1 : 0;

        try {
            out_.writeInt(16); //Size
            out_.writeInt(Response.MSG_PEEK);
            out_.writeInt(senderId);
            out_.writeInt(prio);
            out_.writeInt(queueId);
            out_.flush();

            //Read data
            int msgType = in_.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = in_.readInt();
                if(errorCode == Response.ERR_NO_MESSAGE_IN_QUEUE_EXCEPTION) {
                    throw new NoMessageInQueueException();
                } else if(errorCode == Response.ERR_NO_MESSAGE_FROM_SENDER_EXCEPTION) {
                    throw new NoMessageFromSenderException();
                } else if(errorCode == Response.ERR_MESSAGE_PEEKING_EXCEPTION) {
                    throw new MessagePeekingException();
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException();
                }
            }
            int sender = in_.readInt();
            int receiver = in_.readInt();
            int context = in_.readInt();
            int priority = in_.readInt();
            int msgLength = in_.readInt();
            byte[] message = new byte[msgLength];
            in_.read(message, 0, msgLength);

            //Create object
            Message msg = new Message(receiver, context, priority, new String(message));
            msg.setSender(sender);
            return msg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
