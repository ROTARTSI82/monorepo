package io.github.jgame.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;
import static io.github.jgame.util.UniversalResources.settings;

public class GenericLogger {
    private static final Logger logger = Logger.getLogger("io.github.jgame.logging.GenericLogger");
    private static boolean setupDone = false;

    /**
     * Set the specified logger to the logging level.
     *
     * @param name  Logger name
     * @param level Logging level
     */
    public static void setLogger(String name, Level level) {
        Logger loggerInstance = Logger.getLogger(name);
        loggerInstance.setLevel(level);
    }

    /**
     * Setup the handlers and formatters. Safe to call more than once,
     * as it aborts if it has already been called before.
     *
     * @param CONSOLE_LEVEL Logging level for ConsoleHandler
     * @param LATEST_LEVEL Logging level for output to latest.log
     * @param LOG_LEVEL Logging level for output to "${DATE}.log"
     */
    public static void setup(Level CONSOLE_LEVEL, Level LATEST_LEVEL, Level LOG_LEVEL) {
        if (setupDone) {
            return;
        }
        String handlerFail = JGameStr.getString("logging.GenericLogger.handlerFail");
        String dir = settings.getString("logging.GenericLogger.logDir");

        File logDir = new File(dir);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }
        root.setLevel(Level.ALL);
        GenericFormatter htmlFormat = new GenericFormatter(true);
        GenericFormatter txtFormat = new GenericFormatter(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(txtFormat);
        consoleHandler.setLevel(CONSOLE_LEVEL);
        root.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler(dir + settings.getString("logging.GenericLogger.latestLog"));
            fileHandler.setFormatter(htmlFormat);
            fileHandler.setLevel(LATEST_LEVEL);
            root.addHandler(fileHandler);
        } catch (IOException e) {
            logger.severe(handlerFail + getStackTrace(e));
        }
        try {
            if (LOG_LEVEL != Level.OFF) {
                FileHandler logFileHandler = new FileHandler(fmt(dir + "/%s.html", new Date().toString()));
                logFileHandler.setFormatter(htmlFormat);
                logFileHandler.setLevel(LOG_LEVEL);
                root.addHandler(logFileHandler);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, handlerFail, e);
        }

        /*
        if (Constants.SILENCE_AWT_LOGS) {
            for (String awtLogger : settings.getString("logging.GenericLogger.blockedLoggers").split(",")) {
                setLogger(awtLogger, Level.CONFIG);
            }
        }
         */

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.log(Level.WARNING, fmt(JGameStr.getString("logging.GenericLogger.uncaughtException"), t), e);
        });
        setupDone = true;
    }

    /**
     * Get the stack trace of a Throwable in the form of a string.
     *
     * @param e Throwable
     * @return Stack trace as string.
     */
    public static String getStackTrace(Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
