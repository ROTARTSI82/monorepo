package io.github.jgame;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.UniversalResources.JGameStr;

public class ScrapUtils {
    private static Logger logger = Logger.getLogger(ScrapUtils.class.getName());
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void reload() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public static String getContents() {
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException e) {
            logger.log(Level.WARNING, JGameStr.getString("ScrapUtils.contentFail"), e);
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
