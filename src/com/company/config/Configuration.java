package com.company.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 10/3/13
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Configuration {

    private static Logger LOGGER_ = Logger.getLogger(Configuration.class.getCanonicalName());
    private static Properties PROPS_ = new Properties();

    public static void initConfig(String configFile) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFile));
            PROPS_.load(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            LOGGER_.log(Level.SEVERE, "Couldn't find the configuration file. Stopping.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Couldn't open the configuration file. Stopping.");
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String name) {
        return PROPS_.getProperty(name);
    }

}
