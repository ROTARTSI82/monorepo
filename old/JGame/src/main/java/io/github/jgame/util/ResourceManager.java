package io.github.jgame.util;

import io.github.jgame.image.ImageManager;
import io.github.jgame.mixer.SoundManager;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;

/**
 * The framework for resource manager. Implements recursively adding files from a directory.
 */
public abstract class ResourceManager {
    /**
     * A list of accepted extensions to add in the directory.
     */
    public LinkedList<String> extensions;
    private static Object loader;

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
        assert dir.isDirectory();
        for (File fp : Objects.requireNonNull(dir.listFiles())) {
            if (fp.isDirectory()) {
                internalFromDir(fp, dlen);
            } else if (extensions.contains(ImageManager.getExtension(fp.getAbsolutePath()))) {
                addFile(fp, dlen);
            }
        }
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
        return loader.getClass().getClassLoader().getResource(filename);
    }

    /**
     * Recursive function for adding files from a directory.
     *
     * @param dir Directory to add files from
     * @param baseLen Length of prefix.
     */
    public void internalFromDir(File dir, int baseLen) {
        for (File fp : Objects.requireNonNull(dir.listFiles())) {
            if (fp.isDirectory()) {
                internalFromDir(fp, baseLen);
            } else if (extensions.contains(ImageManager.getExtension(fp.getAbsolutePath()))) {
                addFile(fp, baseLen);
            }
        }
    }
}
