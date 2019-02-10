package io.github.jgame.image;

import io.github.jgame.Constants;
import io.github.jgame.logging.GenericLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    public static BufferedImage toBuffered(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage buffered = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = buffered.createGraphics();
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return buffered;
    }

    public static BufferedImage fromShape(Shape shape, Color color) {
        Rectangle rect = shape.getBounds();
        BufferedImage buffered = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = buffered.createGraphics();
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        g2d.setColor(color);
        g2d.draw(shape);
        g2d.dispose();

        return buffered;
    }

    public static BufferedImage fromText(String string, Font font, Color color) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(string);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(color);
        g2d.drawString(string, 0, fm.getAscent());
        g2d.dispose();
        return img;
    }

    public static String getExtension(String file) {
        return file.substring(file.lastIndexOf("."));
    }

    public static void saveToFile(Image img, String filename) throws IOException {
        File outFile = new File(filename);
        ImageIO.write(toBuffered(img), getExtension(filename), outFile);
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
