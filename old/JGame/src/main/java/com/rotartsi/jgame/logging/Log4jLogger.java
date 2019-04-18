package com.rotartsi.jgame.logging;

import com.rotartsi.jgame.util.ResourceManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

import static com.rotartsi.jgame.util.StringManager.fmt;

public class Log4jLogger {
    private static boolean hasSetup = false;
    private static Logger logger = Logger.getLogger(Log4jLogger.class);

    public static boolean setup(String out, String config) {
        if (!hasSetup) {
            hasSetup = true;
        } else {
            logger.debug(fmt("Skipping setup(\"%s\") because it has already been called before!", out));
            return false;
        }
        JGameFileAppender.setDir("logs/" + out);

        URL logConfig = ResourceManager.loadFile(config);
        PropertyConfigurator.configure(logConfig);
        logger.info(fmt("Using log configuration %s", logConfig));
        return true;
    }

    public static boolean setup(String out) {
        return setup(out, "assets/logging/logProfile.properties");
    }

    /*public static void main(String[] args) {
        setup("4jtest");
        logger.trace("Trace message");
        setup("3jtest");
        logger.debug("Debug message");
        logger.error("Error message");
        logger.fatal("Fatal message");
        logger.info("Info message");
        try {
            int i = 10 / 0;
        } catch (ArithmeticException e) {
            logger.fatal("Error dividing: ", e);
            CrashLogger.logCrash(Level.FATAL, "Error dividing", e);
        }
    }*/
}
