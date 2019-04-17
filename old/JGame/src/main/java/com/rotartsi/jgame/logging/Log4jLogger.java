package com.rotartsi.jgame.logging;

import com.rotartsi.jgame.util.ResourceManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jLogger {
    private static boolean hasSetup = false;
    private static Logger logger = Logger.getLogger(Log4jLogger.class);

    public static boolean setup(String out) {
        if (!hasSetup) {
            hasSetup = true;
        } else {
            logger.debug("Skipping setup() because it has already been called before!");
            return false;
        }
        JGameFileAppender.setDir("logs/" + out);
        System.out.println(ResourceManager.loadFile("assets/logging/logProfile.properties"));
        PropertyConfigurator.configure(ResourceManager.loadFile("assets/logging/logProfile.properties"));
        return true;
    }

    public static void main(String[] args) {
        setup("4jtest");
        logger.trace("Trace message");
        //setup("3jtest");
        logger.debug("Debug message");
        logger.error("Error message");
        logger.fatal("Fatal message");
        logger.info("Info message");
        try {
            int i = 10 / 0;
        } catch (ArithmeticException e) {
            logger.fatal("Error dividing: ", e);
            //CrashLogger.logCrash(Level.FATAL, "Error dividing", e);
        }
    }
}
