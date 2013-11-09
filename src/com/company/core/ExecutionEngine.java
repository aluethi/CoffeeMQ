package com.company.core;

import com.company.database.DaoManager;
import com.company.exception.*;
import com.company.model.Message;
import com.company.model.ModelFactory;

import java.nio.ByteBuffer;

import static com.company.core.Response.*;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/1/13
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionEngine {

    private DaoManager manager_ = new DaoManager();

    public void process(ByteBuffer buffer_) {
        int msgType = buffer_.getInt();

        switch(msgType) {
            case Response.MSG_REGISTER:
                registerClient(buffer_.getInt()).serialize(buffer_);
                break;
            case Response.MSG_DEREGISTER:
                deregisterClient(buffer_.getInt()).serialize(buffer_);
                break;
            case Response.MSG_CREATE_QUEUE:
                createQueue(buffer_.getInt()).serialize(buffer_);
                break;
            case Response.MSG_GET_QUEUE:
                getQueue(buffer_.getInt()).serialize(buffer_);
                break;
            case Response.MSG_DELETE_QUEUE:
                deleteQueue(buffer_.getInt()).serialize(buffer_);
                break;
            case Response.MSG_PUT_INTO_QUEUE:
                put(buffer_).serialize(buffer_);
                break;
            case Response.MSG_GET:
                get(buffer_).serialize(buffer_);
                break;
            case Response.MSG_PEEK:
                peek(buffer_).serialize(buffer_);
                break;
        }
    }

    public Response registerClient(int clientId) {
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getClientDao().createClient(ModelFactory.createClient(clientId));
            manager_.endTransaction();
        } catch (ClientCreationException e) {
            manager_.abortTransaction();
            return err(ERR_CLIENT_CREATION_EXCEPTION);
        } catch (ClientExistsException e) {
            manager_.abortTransaction();
            return err(ERR_CLIENT_EXISTS_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok();
    }

    public Response deregisterClient(int clientId) {
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getClientDao().deleteClient(clientId);
            manager_.endTransaction();
        } catch (ClientDeletionException e) {
            manager_.abortTransaction();
            return err(ERR_CLIENT_DELETION_EXCEPTION);
        } catch (ClientDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok();
    }

    public Response createQueue(int queueId) {
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getQueueDao().createQueue(ModelFactory.createQueue(queueId));
            manager_.endTransaction();
        } catch (QueueCreationException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_CREATION_EXCEPTION);
        } catch (QueueExistsException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_EXISTS_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok();
    }

    private Response getQueue(int queueId) {
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getQueueDao().readQueue(queueId);
            manager_.endTransaction();
        } catch (QueueReadException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_READ_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok();
    }

    private Response deleteQueue(int queueId) {
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getQueueDao().deleteQueue(queueId);
            manager_.endTransaction();
        } catch (QueueDeletionException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_DELETION_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
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
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getMessageDao().enqueueMessage(m);
            manager_.endTransaction();
        } catch (MessageEnqueueingException e) {
            manager_.abortTransaction();
            return err(ERR_MESSAGE_ENQUEUEING_EXCEPTION);
        } catch (SenderDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_SENDER_DOES_NOT_EXIST_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok();
    }

    private Response get(ByteBuffer buffer) {
        int senderId = buffer.getInt();
        int prio = buffer.getInt();
        int queueId = buffer.getInt();
        Message m;
        manager_.beginConnectionScope();
        manager_.beginTransaction();
        try {
            m = manager_.getMessageDao().dequeueMessage(queueId, senderId, (prio != 0));
            manager_.endTransaction();
        } catch (MessageDequeueingException e) {
            manager_.abortTransaction();
            return err(ERR_MESSAGE_DEQUEUEING_EXCEPTION);
        } catch (NoMessageInQueueException e) {
            manager_.abortTransaction();
            return err(ERR_NO_MESSAGE_IN_QUEUE_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } catch (NoMessageFromSenderException e) {
            manager_.abortTransaction();
            return err(ERR_NO_MESSAGE_FROM_SENDER_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok(m);
    }

    private Response peek(ByteBuffer buffer) {
        int senderId = buffer.getInt();
        int prio = buffer.getInt();
        int queueId = buffer.getInt();
        Message m;
        manager_.beginConnectionScope();
        manager_.beginTransaction();
        try {
            m = manager_.getMessageDao().peekMessage(queueId, senderId, (prio != 0));
            manager_.endTransaction();
        } catch (NoMessageInQueueException e) {
            manager_.abortTransaction();
            return err(ERR_NO_MESSAGE_IN_QUEUE_EXCEPTION);
        } catch (NoMessageFromSenderException e) {
            manager_.abortTransaction();
            return err(ERR_NO_MESSAGE_FROM_SENDER_EXCEPTION);
        } catch (MessagePeekingException e) {
            manager_.abortTransaction();
            return err(ERR_MESSAGE_PEEKING_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            manager_.endConnectionScope();
        }
        return ok(m);
    }

}
