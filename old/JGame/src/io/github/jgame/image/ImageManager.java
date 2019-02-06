package io.github.jgame.image;

import io.github.jgame.logging.GenericLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

public class ImageManager {
    private final Logger logger;
    private HashMap<String, Image> images = new HashMap<>();

    public ImageManager() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    public void fromFile(String file, String id) {
        URL url = this.getClass().getClassLoader().getResource(file);
        assert url != null;
        try {
            images.put(id, new ImageIcon(ImageIO.read(url)).getImage());
        } catch (IOException e) {
            logger.warning(String.format("Failed to load %s:\n%s", file, GenericLogger.getStackTrace(e)));
        }
    }

    public void fromUrl(String url, String id) {
        try {
            URL urlObj = new URL(url);
            images.put(id, new ImageIcon(ImageIO.read(urlObj)).getImage());
        } catch (IOException e) {
            logger.warning(String.format("Failed to load %s:\n%s", url, GenericLogger.getStackTrace(e)));
        }
    }

    public Image get(String id) {
        return images.get(id);
    }
}
