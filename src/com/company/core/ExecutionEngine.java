package com.company.core;

import com.company.database.DAO;
import com.company.database.DaoManager;
import com.company.database.PGDatasource;
import com.company.exception.*;
import com.company.model.Message;
import com.company.model.ModelFactory;

import java.nio.ByteBuffer;
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

    private DaoManager manager_ = new DaoManager();

    public void process(ByteBuffer buffer_) {
        int msgType = buffer_.getInt();

        switch(msgType) {
            case MQProtocol.MSG_REGISTER:
                prepareAnswer(buffer_, registerClient(buffer_.getInt()));
                break;
            case MQProtocol.MSG_DEREGISTER:
                prepareAnswer(buffer_, deregisterClient(buffer_.getInt()));
                break;
            case MQProtocol.MSG_CREATE_QUEUE:
                prepareAnswer(buffer_, createQueue(buffer_.getInt()));
                break;
            case MQProtocol.MSG_GET_QUEUE:
                prepareAnswer(buffer_, getQueue(buffer_.getInt()));
                break;
            case MQProtocol.MSG_DELETE_QUEUE:
                prepareAnswer(buffer_, deleteQueue(buffer_.getInt()));
                break;
            case MQProtocol.MSG_PUT_INTO_QUEUE:
                prepareAnswer(buffer_, put(buffer_));
                break;
            case MQProtocol.MSG_GET:
            {
                Response response = get(buffer_);
                if(response != null) {
                    buffer_.clear();
                    prepareAnswer(buffer_, response);
                }
            }
            break;
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
        manager_.beginConnectionScope();
        try {
            manager_.beginTransaction();
            manager_.getClientDao().createClient(ModelFactory.createClient(clientId));
            manager_.endTransaction();
        } catch (ClientCreationException e) {
            manager_.abortTransaction();
            return err(EC_CLIENT_CREATION_EXCEPTION);
        } catch (ClientExistsException e) {
            manager_.abortTransaction();
            // TODO
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
            return err(EC_CLIENT_DELETION_EXCEPTION);
        } catch (ClientDoesNotExistException e) {
            manager_.abortTransaction();
            // TODO
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
            return err(EC_QUEUE_CREATION_EXCEPTION);
        } catch (QueueExistsException e) {
            manager_.abortTransaction();
            // TODO
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
            return err(EC_QUEUE_GET_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            // TODO
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
        } catch (ClientDeletionException e) {
            manager_.abortTransaction();
            return err(EC_QUEUE_DELETION_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            // TODO
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
        } catch (MessageEnqueuingException e) {
            manager_.abortTransaction();
            return err(EC_PUT_EXCEPTION);
        } catch (SenderDoesNotExistException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueueDoesNotExistException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
            //Write message information back to buffer
            buffer.clear();
            buffer.putInt(STATUS_OK);
            buffer.putInt(m.getSender());
            buffer.putInt(m.getReceiver());
            buffer.putInt(m.getContext());
            buffer.putInt(m.getPriority());
            buffer.putInt(m.getMessage().getBytes().length);
            buffer.put(m.getMessage().getBytes());
            manager_.endTransaction();
            return ok();
        } catch (MessageDequeuingException e) {
            manager_.abortTransaction();
            return err(EC_GET_EXCEPTION);
        } catch (NoMessageInQueueException e) {
            manager_.abortTransaction();
            // TODO
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            // TODO
        } catch (NoMessageFromSenderException e) {
            manager_.abortTransaction();
            // TODO
        } finally {
            manager_.endConnectionScope();
        }
        return null;
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
            //Write message information back to buffer
            buffer.clear();
            buffer.putInt(STATUS_OK);
            buffer.putInt(m.getSender());
            buffer.putInt(m.getReceiver());
            buffer.putInt(m.getContext());
            buffer.putInt(m.getPriority());
            buffer.putInt(m.getMessage().getBytes().length);
            buffer.put(m.getMessage().getBytes());
            manager_.endTransaction();
            return ok();
        } catch (NoMessageInQueueException e) {
            manager_.abortTransaction();
            // TODO
        } catch (NoMessageFromSenderException e) {
            manager_.abortTransaction();
            // TODO
        } catch (MessagePeekingException e) {
            manager_.abortTransaction();
            // TODO
        } catch (QueueDoesNotExistException e) {
            manager_.abortTransaction();
            // TODO
        } finally {
            manager_.endConnectionScope();
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
