package com.rotartsi.jgame.logging;

import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JGameFileAppender extends RollingFileAppender {
    private static String dir = "logs/jgame";

    public static void setDir(String dir) {
        JGameFileAppender.dir = dir;
    }

    @Override
    public void setFile(String fileName) {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss.SSS");
        fileName = fileName.replaceAll("%timestamp", format.format(d))
                .replaceAll("%dir", dir);

        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        super.setFile(fileName);
    }
}