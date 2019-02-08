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
        format = "[%s] [%s|%s] [%s]: %s\n";
        String equalSigns = "================================================";
        headFormat = equalSigns + "[%s]" + equalSigns + "\n";
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
                record.getLevel(),
                record.getMessage());
    }
}
