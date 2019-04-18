package com.rotartsi.jgame.util;

import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.mixer.SoundManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

/**
 * The framework for resource manager. Implements recursively adding files from a directory.
 */
public abstract class ResourceManager {
    /**
     * A list of accepted extensions to add in the directory.
     */
    public LinkedList<String> extensions;

    private Logger logger = Logger.getLogger(ResourceManager.class);

    /**
     * Create a new ResourceManager.
     */
    public ResourceManager() {
        extensions = getExtensions();
    }

    /**
     * Override this to define the extensions that are accepted. See {@link #extensions}
     *
     * @return List of extensions.
     */
    public LinkedList<String> getExtensions() {
        return new LinkedList<>();
    }

    /**
     * Add files recursively from a directory.
     *
     * @param dir Directory to add.
     */
    public void fromDir(File dir) {
        int dlen = dir.getAbsolutePath().length();
        internalFromDir(dir, dlen);
    }

    /**
     * Override this handler to add files.
     *
     * @param file    File to be added
     * @param baseLen the length of the prefix to be removed.
     */
    public void addFile(File file, int baseLen) {

    }

    /**
     * Derive an ID from a filename and prefix length.
     *
     * @param file File to get
     * @param baseLen length of prefix
     * @param stripNumbers Whether or not the numbers are stripped from the ID. Useful for random sound playing.
     *                     For example: we could have {@code hurt1.wav} and {@code hurt2.wav} use the same id ("hurt")
     *                     so a random one plays when we call {@code play("hurt")}. See {@link SoundManager}.
     * @return identifier
     */
    public String getId(File file, int baseLen, boolean stripNumbers) {
        String id = file.getAbsolutePath().substring(baseLen + 1).replaceAll("/", ".");
        id = id.substring(0, id.length() - (ImageManager.getExtension(id).length() + 1)); // Remove the extension
        if (stripNumbers) {
            id = id.replaceAll("[0-9]+$", ""); // Remove trailing digits.
        }
        return id;
    }

    /**
     * Load a file from the resources root.
     *
     * @param filename File to load
     * @return URL of the file
     */
    public static URL loadFile(String filename) {
        return ResourceManager.class.getClassLoader().getResource(filename);
    }

    /**
     * Recursive function for adding files from a directory.
     *
     * @param dir Directory to add files from
     * @param baseLen Length of prefix.
     */
    public void internalFromDir(File dir, int baseLen) {
        File[] files = dir.listFiles();
        if (files == null || !dir.isDirectory()) {
            logger.info(fmt(JGameStr.getString("util.ResourceManager.fromDir_nothingToDo"), dir));
            return;
        }

        for (File fp : files) {
            if (fp.isDirectory()) {
                internalFromDir(fp, baseLen);
            } else if (extensions.contains(ImageManager.getExtension(fp.getAbsolutePath()))) {
                addFile(fp, baseLen);
            }
        }
    }
}
