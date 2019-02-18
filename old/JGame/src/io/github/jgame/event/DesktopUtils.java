package io.github.jgame.event;

import io.github.jgame.logging.GenericLogger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class DesktopUtils {
    private static Desktop desktop = Desktop.getDesktop();
    private static Logger logger = Logger.getLogger("io.github.jgame.event.DesktopUtils");
    private static boolean supported = Desktop.isDesktopSupported();

    public static void reload() {
        desktop = Desktop.getDesktop();
        supported = Desktop.isDesktopSupported();
    }

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

    public static boolean mailTo(String sendTo, String subject, String cc, String body, String bcc) {
        try {
            if (supported && desktop.isSupported(Desktop.Action.MAIL)) {
                desktop.mail(new URI(String.format("mailto:%s?subject=%s&cc=%s&body=%s&bcc=%s",
                        sendTo, subject, cc, body, bcc)));
                return true;
            }
        } catch (IOException e) {
            logger.warning("Failed to mail to URI:\n" + GenericLogger.getStackTrace(e));
        } catch (URISyntaxException e) {
            logger.warning("Malformed uri:\n" + GenericLogger.getStackTrace(e));
        }
        return false;
    }

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
