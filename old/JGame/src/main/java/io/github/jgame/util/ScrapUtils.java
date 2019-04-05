package io.github.jgame.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;

/**
 * Utils for accessing clipboards.
 */
public class ScrapUtils {
    /**
     * Logger object ofr event logs
     */
    private static Logger logger = Logger.getLogger(ScrapUtils.class.getName());

    /**
     * The system clipboard. See {@link Clipboard}
     */
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    /**
     * Reload the internal clipboard object ({@link #clipboard})
     */
    public static void reload() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Get the contents of the clipboard as a plain string.
     *
     * @return Clipboard contents as plain string.
     */
    public static String getContents() {
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException e) {
            logger.log(Level.WARNING, JGameStr.getString("util.ScrapUtils.contentFail"), e);
            return "";
        }
    }

    /**
     * Get the clipboard object.
     *
     * @return Clipboard object
     */
    public Clipboard getClipboard() {
        return clipboard;
    }

    /**
     * Copy the string to the system clipboard.
     *
     * @param string String to copy.
     */
    public static void setClipboard(String string) {
        StringSelection selection = new StringSelection(string);
        clipboard.setContents(selection, selection);
    }
}
