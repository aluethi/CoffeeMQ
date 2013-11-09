package com.company.testframework;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/19/13
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Experiment implements Runnable {
    public abstract void setUp(String[] args);
    public abstract void tearDown();
}
