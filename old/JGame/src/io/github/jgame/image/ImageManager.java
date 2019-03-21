package io.github.jgame.image;

import io.github.jgame.Constants;
import io.github.jgame.util.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.Constants.JGameStr;
import static io.github.jgame.util.StringManager.fmt;

/**
 * Utility class for loading and managing BufferedImage assets
 */
public class ImageManager extends ResourceManager {
    /**
     * The logger object used to log events.
     */
    private final Logger logger;

    /**
     * The internal table of images that are loaded.
     */
    private HashMap<String, BufferedImage> images = new HashMap<>();

    /**
     * Create new ImageManager.
     */
    public ImageManager() {
        super();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Get the extension/type of a specified file
     *
     * @param file Filename as string
     * @return extension name
     */
    public static String getExtension(String file) {
        int index = file.lastIndexOf(".");
        if (index != -1) {
            return file.substring(index);
        } else {
            return "";
        }
    }

    /**
     * Copy a BufferedImage.
     *
     * @param bi BufferedImage to copy
     * @return cloned image
     */
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Get a {@code BufferedImage} from text by blitting it to a blank {@code BufferedImage}.
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
     * The extensions of files that are added with {@link #fromDir(File)}
     *
     * @return List of valid extensions.
     */
    @Override
    public LinkedList<String> getExtensions() {
        return new LinkedList<>() {{
            add(".png");
            add(".jpeg");
            add(".jpg");
        }};
    }

    /**
     * Add a BufferedImage to the list.
     *
     * @param id  String identifier.
     * @param img Image
     */
    public void addImage(String id, BufferedImage img) {
        images.put(id, img);
    }

    /**
     * Adding a file for {@link #fromDir(File)}. The identifier is derived from removing {@code baseLen}
     * characters from the front of the string.
     *
     * <b>THE USER SHOULD NEVER CALL THIS FUNCTION!</b>
     *
     * @param file File to add
     * @param baseLen Length of root. (To be removed to get the identifier)
     */
    @Override
    public void addFile(File file, int baseLen) {
        String id = getId(file, baseLen);
        try {
            BufferedImage bi = ImageIO.read(file.toURI().toURL());
            if (bi != null) {
                addImage(id, bi);
            } else {
                logger.warning("Failed to load image: ImageIO.read() returned null (malformed data?)");
            }
        } catch (IOException | IllegalArgumentException e) {
            logger.log(Level.WARNING, fmt(JGameStr.getString("loadFail"), file), e);
        }
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
        try {
            assert url != null;
            addImage(id, ImageIO.read(url));
        } catch (IOException | IllegalArgumentException e) {
            logger.log(Level.WARNING, fmt(JGameStr.getString("loadFail"), file), e);
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
            addImage(id, ImageIO.read(url));
        } catch (IOException e) {
            logger.log(Level.WARNING, fmt(JGameStr.getString("loadFail"), url), e);
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
