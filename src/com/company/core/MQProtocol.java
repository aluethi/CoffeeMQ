package com.company.core;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/15/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class MQProtocol {
    public static final int MSG_REGISTER = 0;
    public static final int MSG_DEREGISTER = 1;
    public static final int MSG_GET_QUEUE = 2;
    public static final int MSG_DELETE_QUEUE = 3;
    public static final int MSG_PUT_INTO_QUEUE = 4;
    public static final int MSG_GET = 5;
    public static final int MSG_PEEK = 6;
    /*public static final int MSG_GET_FROM_QUEUE = 5;
    public static final int MSG_GET_FROM_QUEUE_FROMSENDER = 6;*/
    public static final int MSG_GET_FROM_QUEUE_HIGHESTPRIORITY = 7;
    public static final int MSG_GET_FROM_QUEUE_FROMSENDER_HIGHESTPRIORITY = 8;
    public static final int MSG_PEEK_FROM_QUEUE = 9;
    public static final int MSG_PEEK_FROM_QUEUE_FROMSENDER = 10;
    public static final int MSG_PEEK_FROM_QUEUE_HIGHESTPRIORITY = 11;
    public static final int MSG_PEEK_FROM_QUEUE_FROMSENDER_HIGHESTPRIORITY = 12;

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;





}
