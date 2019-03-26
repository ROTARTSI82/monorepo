package io.github.jgame.util;

import io.github.jgame.image.ImageManager;

import java.io.File;
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
     * @return identifier
     */
    public String getId(File file, int baseLen) {
        String id = file.getAbsolutePath().substring(baseLen + 1).replaceAll("/", ".");
        id = id.substring(0, id.length() - ImageManager.getExtension(id).length()); // Remove the extension
        return id;
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
