package io.github.jgame.logging;

import io.github.jgame.Constants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.*;

import static io.github.jgame.Constants.JGameStr;
import static io.github.jgame.Constants.settings;
import static io.github.jgame.util.StringManager.fmt;

/**
 * Utils for logging with the native java library.
 */
public class GenericLogger {
    /**
     * Internal logger used for logging events.
     */
    private static final Logger logger = Logger.getLogger(GenericLogger.class.getName());

    /**
     * List of active logging paths. Will ignore calls to {@link #setup(Level, Level, Level, String)}
     * if the supplied {@code logPath} is already in {@code loggingPaths}
     */
    private static LinkedList<String> loggingPaths = new LinkedList<>();

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
     * as it aborts if the {@code logPath} is already in {@link #loggingPaths}
     *
     * @param CONSOLE_LEVEL Logging level for ConsoleHandler
     * @param LATEST_LEVEL Logging level for output to latest.log
     * @param LOG_LEVEL Logging level for output to "${DATE}.log"
     * @param logPath Path to output logs to.
     */
    public static void setup(Level CONSOLE_LEVEL, Level LATEST_LEVEL, Level LOG_LEVEL, String logPath) {
        if (loggingPaths.contains(logPath)) {
            logger.info(fmt(
                    "Ignoring GenericLogger.setup(%s, %s, %s, %s). Logger to specified path already setup",
                    CONSOLE_LEVEL, LATEST_LEVEL, LOG_LEVEL, logPath));
            return;
        }
        loggingPaths.add(logPath);
        String handlerFail = JGameStr.getString("logging.GenericLogger.handlerFail");

        File logDir = new File(logPath);
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
            FileHandler fileHandler = new FileHandler(logPath + settings.get("logging.GenericLogger.latestLog"));
            fileHandler.setFormatter(htmlFormat);
            fileHandler.setLevel(LATEST_LEVEL);
            root.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, handlerFail, e);
        }
        try {
            if (LOG_LEVEL != Level.OFF) {
                FileHandler logFileHandler = new FileHandler(fmt(logPath + "/%s.html", new Date().toString()));
                logFileHandler.setFormatter(htmlFormat);
                logFileHandler.setLevel(LOG_LEVEL);
                root.addHandler(logFileHandler);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, handlerFail, e);
        }


        if (Constants.BLOCK_LOGS) {
            for (String awtLogger : settings.get("logging.GenericLogger.blockedLoggers").split(",")) {
                setLogger(awtLogger, Level.CONFIG);
            }
        }


        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.log(Level.WARNING, fmt(JGameStr.getString("logging.GenericLogger.uncaughtException"), t), e);
        });
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
