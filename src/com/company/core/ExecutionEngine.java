package com.company.core;

import com.company.database.DAO;
import com.company.database.PGDatasource;
import com.company.exception.ClientCreationException;
import com.company.exception.ClientDeletionException;
import com.company.model.ModelFactory;

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
        }
    }

    private Error getQueue(int queueId) {
        LOGGER_.log(Level.INFO, "Get queue: " + queueId);

    }

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
