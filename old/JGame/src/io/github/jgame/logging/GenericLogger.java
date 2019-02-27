package io.github.jgame.logging;

import io.github.jgame.Constants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

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
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }
        root.setLevel(Level.ALL);
        GenericFormatter formatterTxt = new GenericFormatter();

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatterTxt);
        consoleHandler.setLevel(CONSOLE_LEVEL);
        root.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler("logs/latest.log");
            fileHandler.setFormatter(formatterTxt);
            fileHandler.setLevel(LATEST_LEVEL);
            root.addHandler(fileHandler);
        } catch (IOException e) {
            logger.severe("Failed to add handler: \n" + getStackTrace(e));
        }
        try {
            if (LOG_LEVEL != Level.OFF) {
                FileHandler logFileHandler = new FileHandler("logs/" + new Date().toString() + ".log");
                logFileHandler.setFormatter(formatterTxt);
                logFileHandler.setLevel(LOG_LEVEL);
                root.addHandler(logFileHandler);
            }
        } catch (IOException e) {
            logger.severe("Failed to add handler: \n" + getStackTrace(e));
        }

        if (Constants.SILENCE_AWT_LOGS) {
            setLogger("java.awt", Level.CONFIG);
            setLogger("javax.swing", Level.CONFIG);
            setLogger("sun.awt", Level.CONFIG);
            setLogger("sun.lwawt", Level.CONFIG);
        }
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
