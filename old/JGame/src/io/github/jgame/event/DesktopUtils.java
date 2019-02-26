package io.github.jgame.event;

import io.github.jgame.logging.GenericLogger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Easy way to perform actions such as opening the webbrowser and printing files to the local printer.
 */
public class DesktopUtils {
    private static Desktop desktop = Desktop.getDesktop();
    private static Logger logger = Logger.getLogger("io.github.jgame.event.DesktopUtils");
    private static boolean supported = Desktop.isDesktopSupported();

    /**
     * Reload internal desktop object.
     */
    public static void reload() {
        desktop = Desktop.getDesktop();
        supported = Desktop.isDesktopSupported();
    }

    /**
     * Open URI in webbrowser if it is supported.
     *
     * @param uri URI to open in webbrowser
     * @return true if successful
     */
    public static boolean openBrowser(URI uri) {
        try {
            if (supported && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
                return true;
            }
        } catch (IOException e) {
            logger.warning("Failed to open webbrowser:\n" + GenericLogger.getStackTrace(e));
        }
        return false;
    }

    /**
     * Print file to local printer if it is supported.
     *
     * @param file File to print
     * @return true if successful
     */
    public static boolean printFile(File file) {
        try {
            if (supported && desktop.isSupported(Desktop.Action.PRINT)) {
                desktop.print(file);
                return true;
            }
        } catch (IOException e) {
            logger.warning("Failed to print file:\n" + GenericLogger.getStackTrace(e));
        }
        return false;
    }

    /**
     * Open specified file for editing in default editor program. (if it is supported)
     *
     * @param file File to open
     * @return true if successful
     */
    public static boolean openFileEdit(File file) {
        try {
            if (supported && desktop.isSupported(Desktop.Action.EDIT)) {
                desktop.edit(file);
                return true;
            }
        } catch (IOException e) {
            logger.warning("Failed to open file for editing:\n" + GenericLogger.getStackTrace(e));
        }
        return false;
    }

    /**
     * Try to open URI in the default mail client. See {@link #mailTo(String, String, String, String, String)}
     *
     * @param uri URI to open in the mail client
     * @return true if successful
     */
    public static boolean mailTo(URI uri) {
        try {
            if (supported && desktop.isSupported(Desktop.Action.MAIL)) {
                desktop.mail(uri);
                return true;
            }
        } catch (IOException e) {
            logger.warning("Failed to mail to URI:\n" + GenericLogger.getStackTrace(e));
        }
        return false;
    }

    /**
     * Format URI from arguments and call {@link #mailTo(URI)}
     * URI is formatted from the string {@code "mailto:%s?subject=%s&cc=%s&body=%s&bcc=%s"}
     *
     * @param sendTo  Address(es) to send mail to.
     * @param subject Subject of email
     * @param cc      CC
     * @param body    Body of email
     * @param bcc     BCC
     * @return true if successful
     */
    public static boolean mailTo(String sendTo, String subject, String cc, String body, String bcc) {
        String uriStr = String.format("mailto:%s?subject=%s&cc=%s&body=%s&bcc=%s", sendTo, subject, cc, body, bcc);
        try {
            return mailTo(new URI(uriStr));
        } catch (URISyntaxException e) {
            logger.warning(String.format("Failed to construct URI from '%s'", uriStr));
        }
        return false;
    }

    /**
     * Try to open the default mail client (not to a specific URI, though. See {@link #mailTo(URI)}
     *
     * @return True if successful
     */
    public static boolean openMail() {
        try {
            if (supported && desktop.isSupported(Desktop.Action.MAIL)) {
                desktop.mail();
                return true;
            }
        } catch (IOException e) {
            logger.warning("Failed to open mail:\n" + GenericLogger.getStackTrace(e));
        }
        return false;
    }
}
