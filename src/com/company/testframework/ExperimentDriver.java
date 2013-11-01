package com.company.testframework;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/18/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentDriver {

    public static void main(String[] args) {
        if(args.length <= 1) {
            System.out.println("Experiment argument missing.");
        }

        ExperimentDriver driver = new ExperimentDriver(args[1]);
    }

    public ExperimentDriver(String className) {
        init(className);
    }

    private void init(String className) {
        System.out.println("Loading experiment class: " + className);
        try {
            Class<Experiment> expClass = (Class<Experiment>) ExperimentDriver.class.getClassLoader().loadClass(className);
            Experiment experiment = expClass.newInstance();

            System.out.println("Starting experiment...");
            experiment.setUp();
            new Thread(experiment).start();
            experiment.tearDown();
            System.out.println("End of experiment.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
