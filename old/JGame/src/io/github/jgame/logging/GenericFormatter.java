package io.github.jgame.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.settings;

/**
 * Generic format for .log
 */
public class GenericFormatter extends Formatter {
    SimpleDateFormat dateFormat = new SimpleDateFormat(settings.getString("logging.GenericFormatter.dateFormat"));
    private String format;
    private String headFormat;
    private Date date = new Date();
    private boolean html;

    public GenericFormatter(boolean useHTML) {
        html = useHTML;
        if (useHTML) {
            format = settings.getString("logging.GenericFormatter.htmlLogFormat");
            headFormat = settings.getString("logging.GenericFormatter.htmlLogHead");
        } else {
            format = settings.getString("logging.GenericFormatter.logFormat");
            headFormat = settings.getString("logging.GenericFormatter.logHead");
        }
    }

    /**
     * Get header. Just a bunch of equal signs and the date and time.
     * <p>
     * e.g.
     * "================================================[Mon Jan 01 12:05:30 PST 2020]================================================"
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
     * e.g. "[2020-01-01 12:05:30] [Thread-16] [io.github.jgame.logging.GenericFormatter|testLogMessage] [INFO]: Test message\n"
     *
     * @param record {@link LogRecord}
     * @return String
     */
    @Override
    public String format(LogRecord record) {
        date.setTime(record.getMillis());
        Throwable exc = record.getThrown();
        String stdMsg = fmt(format,
                dateFormat.format(date),
                record.getThreadID(),
                record.getLoggerName(),
                record.getSourceClassName(),
                record.getSourceMethodName(),
                record.getLevel(),
                record.getMessage());

        if (exc == null) {
            return stdMsg;
        } else {
            if (html) {
                return stdMsg + formatHTML(exc);
            } else {
                return stdMsg + "\n" + GenericLogger.getStackTrace(exc);
            }
        }
    }

    private String formatHTML(Throwable e) {
        return fmt(settings.getString("logging.GenericFormatter.htmlException"),
                GenericLogger.getStackTrace(e).replace(" ", "&nbsp;")
                        .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                        .replace("\n", "<br/>\n"));
    }
}
