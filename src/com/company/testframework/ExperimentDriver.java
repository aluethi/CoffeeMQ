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
            System.out.println("Experiment argument missing: Experiment name and host needed.");
        }
        if(args[1].equals("ScalableConsumerExperiment")) {
            if(args.length <= 3) {
                System.out.println("Experiment argument missing: Prefix of consumer name and number of consumers needed.");
            }
        }
        if(args[1].equals("ScalableProducerExperiment")) {
            if(args.length <= 3) {
                System.out.println("Experiment argument missing: Prefix of producer name and number of producers needed.");
            }
        }

        ExperimentDriver driver = new ExperimentDriver(args);
    }

    public ExperimentDriver(String[] args) {
       init(args);
    }

    private void init(String[] args) {
        System.out.println("Loading experiment class: " + args[0]);
        try {
            Class<Experiment> expClass = (Class<Experiment>) ExperimentDriver.class.getClassLoader().loadClass("com.company.testframework.experiments." + args[0]);
            Experiment experiment = expClass.newInstance();

            System.out.println("Starting experiment...");
            experiment.setUp(args);
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
