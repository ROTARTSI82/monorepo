package io.github.jgame.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GenericFormatter extends Formatter {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String format;
    private String headFormat;
    private Date date = new Date();

    public GenericFormatter() {
        format = "[%s] [%s|%s] [%s|%s]: %s\n";
        String equalSigns = repeat("=", 16);
        headFormat = equalSigns + "[%s]" + equalSigns + "\n";
    }

    static String repeat(String str, int n) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < n; i++) {
            buf.append(str);
        }
        return buf.toString();
    }

    @Override
    public String getHead(Handler handler) {
        return String.format(headFormat, new Date().toString());
    }

    @Override
    public String getTail(Handler handler) {
        return "";
    }

    @Override
    public String format(LogRecord record) {
        date.setTime(record.getMillis());
        return String.format(format,
                dateFormat.format(date),
                record.getSourceClassName(),
                record.getSourceMethodName(),
                record.getLoggerName(),
                record.getLevel(),
                record.getMessage());
    }
}
