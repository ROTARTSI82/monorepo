package io.github.jgame.image;

import io.github.jgame.Constants;
import io.github.jgame.logging.GenericLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

/**
 * Utility class for loading and managing BufferedImage assets
 */
public class ImageManager {
    private final Logger logger;
    private HashMap<String, BufferedImage> images = new HashMap<>();

    public ImageManager() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Convert an {@code Image} to a {@code BufferedImage} by blitting the image onto a buffered version.
     *
     * @param img Image to convert
     * @return BufferedImage version
     */
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

    /**
     * Get a {@code BufferedImage} from a {@code Shape} by blitting it to the image.
     *
     * @param shape Shape to convert
     * @param color Color to blit the shape in
     * @return Converted {@code BufferedImage}
     */
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

    /**
     * Get a {@code BufferedImage} from text by blitting it.
     *
     * @param string Text to convert
     * @param font   Font to render the text in
     * @param color  Color to render the text in
     * @return {@code BufferedImage}
     */
    public static BufferedImage fromText(String string, Font font, Color color) {
        // Create a temporary image to get the size of the text.
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

    /**
     * Get the extension/type of a specified file
     *
     * @param file Filename as string
     * @return extension name
     */
    public static String getExtension(String file) {
        return file.substring(file.lastIndexOf("."));
    }

    /**
     * Save a buffered image to a file.
     *
     * @param img BufferedImage to save.
     * @param filename File to save to
     * @throws IOException File opening/writing may fail.
     */
    public static void saveToFile(Image img, String filename) throws IOException {
        File outFile = new File(filename);
        ImageIO.write(toBuffered(img), getExtension(filename), outFile);
    }

    /**
     * Load an image from file using the {@link ClassLoader} and put it into the {@code HashMap}
     * with the key {@code id}
     *
     * @param file File to load as string
     * @param id Key for HashMap
     */
    public void fromFile(String file, String id) {
        URL url = this.getClass().getClassLoader().getResource(file);
        assert url != null;
        try {
            images.put(id, ImageIO.read(url));
        } catch (IOException e) {
            logger.warning(fmt(JGameStr.getString("loadFail"),
                    file, GenericLogger.getStackTrace(e)));
        }
    }

    /**
     * Load an image from an URL and put it into the {@code HashMap} with a key of {@code id}
     *
     * @param url URL to load
     * @param id  ID to put it at
     */
    public void fromUrl(URL url, String id) {
        try {
            images.put(id, ImageIO.read(url));
        } catch (IOException e) {
            logger.warning(fmt(JGameStr.getString("loadFail"),
                    url, GenericLogger.getStackTrace(e)));
        }
    }

    /**
     * Get the {@code BufferedImage} from the {@code HashMap}
     *
     * @param id BufferedImage to load
     * @return BufferedImage
     */
    public BufferedImage get(String id) {
        return images.get(id);
    }
}
