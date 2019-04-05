package io.github.jgame.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;

/**
 * Utility functions for creating/deleteing files/directories.
 */
public class FileUtils {

    private static Logger logger = Logger.getLogger(FileUtils.class.getName());

    /**
     * Create an empty file (parent directories will be created if they dont exist.)
     *
     * @param file File to create
     * @return True if successful.
     */
    public static boolean touchFile(File file) {
        file.getParentFile().mkdirs();

        try {
            return file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, JGameStr.getString("util.FileUtils.touchFileFail"), e);
        }
        return false;
    }

    /**
     * Recursively delete the directory and all of it's files.
     *
     * @param dir Directory to delete.
     * @return True if successful.
     */
    public static boolean deleteDir(File dir) {
        File[] files = dir.listFiles();
        boolean ret = true;

        if (files == null) {
            return false;
        }

        for (File i : files) {
            if (i.isDirectory() && !Files.isSymbolicLink(i.toPath())) {
                deleteDir(i);
            } else if (i.isFile()) {
                if (!i.delete()) {
                    ret = false;
                }
            }
        }
        if (!dir.delete()) {
            return false;
        }
        return ret;
    }

    /**
     * Create an empty file (parent directories will be created if they dont exist.)
     * It is recommended that you use {@link #touchFile(File)} directly instead of relying on this method.
     *
     * @param file File to create
     * @return True if successful.
     */
    public static boolean touchFile(String file) {
        return touchFile(new File(file));
    }

    /**
     * Create an empty folder from a string.
     *
     * @param folder Folder to create
     * @return True if successful.
     */
    public static boolean touchFolder(String folder) {
        File f = new File(folder);
        return f.mkdirs();
    }

}
