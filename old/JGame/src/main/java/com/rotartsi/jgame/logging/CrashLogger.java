package com.rotartsi.jgame.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

import static com.rotartsi.jgame.Constants.settings;

public class CrashLogger {
    private static Logger logger = Logger.getLogger(CrashLogger.class);

    public static void logCrash(Priority level, Object msg, Throwable err) {
        JGameFileAppender fileAppender = new JGameFileAppender();
        fileAppender.setName("CrashReporter");
        fileAppender.setFile("%dir/crashReports/crash-report %timestamp.log");
        fileAppender.setLayout(new PatternLayout(settings.get("logging.crashFormat",
                "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n")));
        fileAppender.setThreshold(Level.ALL);
        fileAppender.setAppend(true);
        fileAppender.activateOptions();

        logger.addAppender(fileAppender);
        logger.log(level, msg, err);
        logger.removeAppender(fileAppender);
    }
}
