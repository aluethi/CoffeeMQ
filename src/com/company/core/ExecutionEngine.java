package com.company.core;

import com.company.database.DAO;
import com.company.database.PGDatasource;
import com.company.exception.*;
import com.company.model.Client;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.core.Response.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/1/13
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionEngine {

    private static Logger LOGGER_ = Logger.getLogger(ExecutionEngine.class.getCanonicalName());

    private DAO dao_ = new DAO(new PGDatasource());

    public void process(ByteBuffer buffer_) {
        LOGGER_.log(Level.INFO, "[Buffer] position: " + buffer_.position() + " limit: " + buffer_.limit());
        int msgType = buffer_.getInt();
        LOGGER_.log(Level.INFO, "In process() with msgType: " + msgType);

        switch(msgType) {
            case MQProtocol.MSG_REGISTER:
            {
                prepareAnswer(buffer_, registerClient(buffer_.getInt()));
            }
            break;
            case MQProtocol.MSG_DEREGISTER:
            {
                prepareAnswer(buffer_, deregisterClient(buffer_.getInt()));
            }
            break;
            case MQProtocol.MSG_CREATE_QUEUE:
            {
                prepareAnswer(buffer_, createQueue(buffer_.getInt()));
            }
            break;
            case MQProtocol.MSG_GET_QUEUE:
            {
                prepareAnswer(buffer_, getQueue(buffer_.getInt()));
            }
            break;
            case MQProtocol.MSG_DELETE_QUEUE:
            {
                prepareAnswer(buffer_, deleteQueue(buffer_.getInt()));
            }
            break;
            case MQProtocol.MSG_PUT_INTO_QUEUE:
            {
                prepareAnswer(buffer_, put(buffer_));
            }
            break;
            case MQProtocol.MSG_GET:
            {
                Response response = get(buffer_);
                if(response != null) {
                    buffer_.clear();
                    prepareAnswer(buffer_, response);
                }
            }
            case MQProtocol.MSG_PEEK:
            {
                Response response = peek(buffer_);
                if(response != null) {
                    buffer_.clear();
                    prepareAnswer(buffer_, response);
                }
            }
            break;
        }
    }

    public Response registerClient(int clientId) {
        LOGGER_.log(Level.INFO, "Registering client " + clientId);
        try {
            dao_.createClient(ModelFactory.createClient(clientId));
        } catch (ClientCreationException e) {
            return err(EC_CLIENT_CREATION_EXCEPTION);
        }
        return ok();
    }

    public Response deregisterClient(int clientId) {
        // TODO: change DAO to only use clientId instead of client model to delete a client
        LOGGER_.log(Level.INFO, "Deregistering client " + clientId);
        try {
            dao_.deleteClient(ModelFactory.createClient(clientId));
        } catch (ClientDeletionException e) {
            return err(EC_CLIENT_DELETION_EXCEPTION);
        }
        return ok();
    }

    public Response createQueue(int queueId) {
        LOGGER_.log(Level.INFO, "Creating queue " + queueId);
        try {
            dao_.createQueue(ModelFactory.createQueue(queueId));
        } catch (QueueCreationException e) {
            return err(EC_QUEUE_CREATION_EXCEPTION);
        }
        return ok();
    }

    private Response getQueue(int queueId) {
        LOGGER_.log(Level.INFO, "Get queue " + queueId);
        try {
            dao_.getQueue(queueId);
        } catch (GetQueueException e) {
            return err(EC_QUEUE_GET_EXCEPTION);
        }
        return ok();
    }

    private Response deleteQueue(int queueId) {
        LOGGER_.log(Level.INFO, "Delete queue " + queueId);
        Queue q = ModelFactory.createQueue(queueId);
        try {
            dao_.deleteQueue(q);
        } catch (QueueDeletionException e) {
            return err(EC_QUEUE_DELETION_EXCEPTION);
        }
        return ok();
    }

    private Response put(ByteBuffer buffer) {
        int queueId = buffer.getInt();
        int senderId = buffer.getInt();
        int receiverId = buffer.getInt();
        int context = buffer.getInt();
        int prio = buffer.getInt();
        int msgLength = buffer.getInt();
        byte[] msg = new byte[msgLength];
        buffer.get(msg);
        Message m = ModelFactory.createMessage(senderId, receiverId, queueId, context, prio, new String(msg));
        try {
            dao_.enqueueMessage(m);
        } catch (MessageEnqueuingException e) {
            return err(EC_PUT_EXCEPTION);
        }
        return ok();
    }
/*
    private Response get(ByteBuffer buffer) {

    }
*/
    private Response get(ByteBuffer buffer) {
        int senderId = buffer.getInt();
        int prio = buffer.getInt();
        int queueId = buffer.getInt();
        Queue q = ModelFactory.createQueue(queueId);
        Client s = null;
        Message m = null;
        if (senderId != 0) {
            s = ModelFactory.createClient(senderId);
        }
        try {
            if (senderId == 0) {
                if (prio == 0) {
                    m = dao_.dequeueMessage(q, false);
                } else {
                    m = dao_.dequeueMessage(q, true);
                }
            } else {
                if (prio == 0) {
                    m = dao_.dequeueMessage(q, s, false);
                } else {
                    m = dao_.dequeueMessage(q, s, true);
                }
            }
            //Write message information back to buffer
            buffer.clear();
            buffer.putInt(STATUS_OK);
            buffer.putInt(m.getSender());
            buffer.putInt(m.getReceiver());
            buffer.putInt(m.getContext());
            buffer.putInt(m.getPriority());
            buffer.putInt(m.getMessage().length());
            buffer.put(m.getMessage().getBytes());
        } catch (MessageDequeuingException e) {
            return err(EC_GET_EXCEPTION);
        }
        return null;
    }

    private Response peek(ByteBuffer buffer) {
        int senderId = buffer.getInt();
        int prio = buffer.getInt();
        int queueId = buffer.getInt();
        Queue q = ModelFactory.createQueue(queueId);
        Client s = null;
        Message m = null;
        if (senderId != 0) {
            s = ModelFactory.createClient(senderId);
        }
        try {
            if (senderId == 0) {
                if (prio == 0) {
                    dao_.peekMessage(q, false);
                } else {
                    dao_.peekMessage(q, true);
                }
            } else {
                if (prio == 0) {
                    dao_.peekMessage(q, s, false);
                } else {
                    dao_.peekMessage(q, s, true);
                }
            }
            //Write message information back to buffer
            buffer.clear();
            buffer.putInt(STATUS_OK);
            buffer.putInt(m.getSender());
            buffer.putInt(m.getReceiver());
            buffer.putInt(m.getContext());
            buffer.putInt(m.getPriority());
            buffer.putInt(m.getMessage().length());
            buffer.put(m.getMessage().getBytes());
        } catch (MessageDequeuingException e) {
            return err(EC_GET_EXCEPTION);
        }
        return null;
    }


    private void prepareAnswer(ByteBuffer buffer, Response response) {
        buffer.clear();
        buffer.putInt(response.getStatus());
        if(response.getStatus() != STATUS_OK) {
            buffer.putInt(response.getErrorCode());
        }
    }
}
