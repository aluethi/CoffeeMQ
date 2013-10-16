package com.company.core;

import com.company.database.DAO;
import com.company.database.PGDatasource;
import com.company.exception.ClientCreationException;
import com.company.exception.ClientDeletionException;
import com.company.exception.MessageEnqueuingException;
import com.company.exception.QueueDeletionException;
import com.company.model.Message;
import com.company.model.ModelFactory;
import com.company.model.Queue;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.core.Error.*;

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
        int msgType = buffer_.getInt();

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
            case MQProtocol.MSG_GET: // Fall through
            case MQProtocol.MSG_PEEK:
            {
                prepareAnswer(buffer_, get(buffer_));
            }
            break;
        }
    }

    private Error get(ByteBuffer buffer) {
        return ok();
    }

    private Error put(ByteBuffer buffer) {
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

    private Error deleteQueue(int queueId) {
        LOGGER_.log(Level.INFO, "Delete queue " + queueId);
        Queue q = ModelFactory.createQueue(queueId);
        try {
            dao_.deleteQueue(q);
        } catch (QueueDeletionException e) {
            return err(EC_QUEUE_DELETION_EXCEPTION);
        }
        return ok();
    }

    private Error getQueue(int queueId) {
        LOGGER_.log(Level.INFO, "Get queue " + queueId);
        return ok();
    }

    private Error

    public Error registerClient(int clientId) {
        LOGGER_.log(Level.INFO, "Registering client " + clientId);
        try {
            dao_.createClient(ModelFactory.createClient(clientId));
        } catch (ClientCreationException e) {
            return err(EC_CLIENT_CREATION_EXCEPTION);
        }
        return ok();
    }

    public Error deregisterClient(int clientId) {
        // TODO: change DAO to only use clientId instead of client model to delete a client
        LOGGER_.log(Level.INFO, "Deregistering client " + clientId);
        try {
            dao_.deleteClient(ModelFactory.createClient(clientId));
        } catch (ClientDeletionException e) {
            return err(EC_CLIENT_DELETION_EXCEPTION);
        }
        return ok();
    }

    private void prepareAnswer(ByteBuffer buffer, Error error) {
        buffer.putInt(error.getStatus());
        buffer.putInt(error.getErrorCode());
    }

}
