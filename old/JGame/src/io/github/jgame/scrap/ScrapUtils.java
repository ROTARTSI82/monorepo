package io.github.jgame.scrap;

import io.github.jgame.logging.GenericLogger;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.logging.Logger;

public class ScrapUtils {
    private static Logger logger = Logger.getLogger("io.github.jgame.scrap.ScrapUtils");

    public static void setClipboard(String string) {
        StringSelection selection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    public static String getContents() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            logger.info(String.format("Failed to get contents:\n%s", GenericLogger.getStackTrace(e)));
            return "";
        }
    }
}
