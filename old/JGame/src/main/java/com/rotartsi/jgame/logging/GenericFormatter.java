package com.rotartsi.jgame.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.rotartsi.jgame.Constants.settings;
import static com.rotartsi.jgame.util.StringManager.fmt;

/**
 * Generic formatter for logging.
 */
public class GenericFormatter extends Formatter {
    /**
     * How the date would be formatted in log messages.
     */
    private SimpleDateFormat dateFormat =
            new SimpleDateFormat(settings.get("logging.GenericFormatter.dateFormat"));

    /**
     * Format for the log messages.
     */
    private String format;

    /**
     * Format for the head of the log file.
     */
    private String headFormat;

    /**
     * Current date. (Will be updated every call to {@link #format(LogRecord)})
     */
    private Date date = new Date();

    /**
     * Does this output HTML?
     */
    private boolean html;

    /**
     * Colors of the table entries corresponding to the logging levels. In the format of css color styling
     * e.g. #ffffff is white. #ff0000 is red. etc
     */
    private HashMap<Level, String> colors = new HashMap<>() {{
        put(Level.FINEST, "#eee9e0");
        put(Level.FINER, "#eee9e0");
        put(Level.FINE, "#eee9e0");
        put(Level.CONFIG, "#eee9e0");

        put(Level.INFO, "#ffffff");

        put(Level.WARNING, "#ffeecc");

        put(Level.SEVERE, "#f7a699");
    }};

    /**
     * Create a new formatter
     *
     * @param useHTML Does this formatter output HTML or plain text?
     */
    public GenericFormatter(boolean useHTML) {
        html = useHTML;
        if (useHTML) {
            format = settings.get("logging.GenericFormatter.htmlLogFormat");
            headFormat = settings.get("logging.GenericFormatter.htmlLogHead");
        } else {
            format = settings.get("logging.GenericFormatter.logFormat");
            headFormat = settings.get("logging.GenericFormatter.logHead");
        }
    }

    /**
     * Get header. Just a bunch of equal signs and the date and time.
     * <p>
     * e.g.
     * "================================================[Mon Jan 01 12:05:30 PST 2020]================================================"
     *
     * Similar format used for HTML
     *
     * @param handler {@link Handler}
     * @return Date and time
     */
    @Override
    public String getHead(Handler handler) {
        return fmt(headFormat, new Date().toString());
    }

    /**
     * No tail. Returns empty string.
     *
     * @param handler {@link Handler}
     * @return Empty string
     */
    @Override
    public String getTail(Handler handler) {
        return "";
    }

    /**
     * Formats logs in the following format:
     * {@code "[yyyy-MM-dd HH:mm:ss] [threadID] [sourceClass|sourceMethod] [LOG LEVEL]: MESSAGE\n"}
     * e.g. "[2020-01-01 12:05:30] [Thread-16] [GenericFormatter|testLogMessage] [INFO]: Test message\n"
     *
     * Similar format used for HTML
     *
     * @param record {@link LogRecord}
     * @return String
     */
    @Override
    public String format(LogRecord record) {
        date.setTime(record.getMillis());
        Throwable exc = record.getThrown();

        if (exc == null) {
            if (html) {
                return fmt(format, colors.get(record.getLevel()),
                        dateFormat.format(date),
                        record.getThreadID(),
                        record.getLoggerName(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getLevel(),
                        record.getMessage());
            } else {
                return fmt(format,
                        dateFormat.format(date),
                        record.getThreadID(),
                        record.getLoggerName(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getLevel(),
                        record.getMessage());
            }
        } else {
            if (html) {

                return fmt(format, colors.get(record.getLevel()),
                        dateFormat.format(date),
                        record.getThreadID(),
                        record.getLoggerName(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getLevel(),
                        record.getMessage()) + formatHTML(exc);
            } else {
                return fmt(format,
                        dateFormat.format(date),
                        record.getThreadID(),
                        record.getLoggerName(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getLevel(),
                        record.getMessage()) + "\n" + GenericLogger.getStackTrace(exc);
            }
        }
    }

    /**
     * Get the HTML for the exception. Just a red table entry with white text containing the
     * {@code {@link GenericLogger}.getStackTrace()} of the throwable
     *
     * @param e Throwable
     * @return formatted string (in HTML)
     */
    private String formatHTML(Throwable e) {
        return fmt(settings.get("logging.GenericFormatter.htmlException"),
                GenericLogger.getStackTrace(e).replace(" ", "&nbsp;")
                        .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                        .replace("\n", "<br/>\n"));
    }
}
