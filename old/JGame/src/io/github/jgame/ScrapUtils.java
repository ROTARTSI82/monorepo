package io.github.jgame;

import io.github.jgame.logging.GenericLogger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.logging.Logger;

public class ScrapUtils {
    private static Logger logger = Logger.getLogger("io.github.jgame.ScrapUtils");
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void reload() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public static String getContents() {
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            logger.info(String.format("Failed to get contents:\n%s", GenericLogger.getStackTrace(e)));
            return "";
        }
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public static void setClipboard(String string) {
        StringSelection selection = new StringSelection(string);
        clipboard.setContents(selection, selection);
    }
}
