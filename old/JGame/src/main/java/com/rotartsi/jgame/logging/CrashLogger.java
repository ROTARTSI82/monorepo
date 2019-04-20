package com.rotartsi.jgame.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.rotartsi.jgame.util.StringManager.fmt;

public class CrashLogger {
    private static Logger logger = LogManager.getLogger(CrashLogger.class);
    private static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void logCrash(String msg, Throwable err) {
        logger.entry(msg, err);
        File dir = new File("logs/crash-reports");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String date = fmt.format(new Date());
        File fp = new File(fmt("logs/crash-reports/crash-report %s.log", date));
        try {
            PrintWriter out = new PrintWriter(fp);
            out.write("DATETIME: ");
            out.println(date);

            out.write("THREAD: ");
            out.println(Thread.currentThread().getName());

            out.write("\n\n");
            out.println(msg);
            out.write("\n\n");

            out.println(getStackTrace(err));
        } catch (IOException e) {
            logger.fatal("Failed to write crash report: ", e);
            logger.fatal("Crash data will be logged here instead.");
            logger.fatal("CRASH_MSG={}, EXC=", msg, err);
        }

    }

    public static void main(String[] args) {
        try {
            recursive();
        } catch (StackOverflowError e) {
            logCrash("Oh no! This is a test crash for stackOverflows!", e);
        }
    }

    public static void recursive() {
        recursive();
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
