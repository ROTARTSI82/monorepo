package io.github.jgame.util;

import io.github.jgame.image.ImageManager;

import java.io.File;
import java.util.LinkedList;

public abstract class ResourceManager {
    public LinkedList<String> extensions;

    public ResourceManager() {
        extensions = getExtensions();
    }

    public LinkedList<String> getExtensions() {
        return new LinkedList<>();
    }

    public void fromDir(File dir) {
        int dlen = dir.getAbsolutePath().length();
        assert dir.isDirectory();
        for (File fp : dir.listFiles()) {
            if (fp.isDirectory()) {
                internalFromDir(fp, dlen);
            } else if (extensions.contains(ImageManager.getExtension(fp.getAbsolutePath()))) {
                addFile(fp, dlen);
            }
        }
    }

    public void addFile(File file, int baseLen) {

    }

    public String getId(File file, int baseLen) {
        String id = file.getAbsolutePath().substring(baseLen + 1).replaceAll("/", ".");
        id = id.substring(0, id.length() - ImageManager.getExtension(id).length()); // Remove the extension
        return id;
    }

    public void internalFromDir(File dir, int baseLen) {
        for (File fp : dir.listFiles()) {
            if (fp.isDirectory()) {
                internalFromDir(fp, baseLen);
            } else if (extensions.contains(ImageManager.getExtension(fp.getAbsolutePath()))) {
                addFile(fp, baseLen);
            }
        }
    }
}
