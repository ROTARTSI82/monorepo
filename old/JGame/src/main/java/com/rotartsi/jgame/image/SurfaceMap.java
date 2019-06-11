package com.rotartsi.jgame.image;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.math.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Extension of {@link java.awt.image.BufferedImage}
 * <p>
 * For scaling coordinates between two panes and blitting it to the screen.
 */
public class SurfaceMap {

    /**
     * Graphics2D to blit to. (Blit to this to blit to the entire SurfaceMap)
     */
    public Graphics2D g2d;
    /**
     * The raw image.
     */
    public BufferedImage img;
    /**
     * Resized image ready for blitting!
     */
    private BufferedImage resized;
    /**
     * Where to blit the topleft of the {@link #img} for the map to be scrolled properly
     * <p>
     * (For the correct position on the map to be centered onscreen propery)
     */
    private Vector2 scroll;

    /**
     * @param x    Width of {@code SurfaceMap}
     * @param y    Height of {@code SurfaceMap}
     * @param type Type of {@code BufferedImage} (default BufferedImage.TYPE_INT_ARGB)
     */
    public SurfaceMap(int x, int y, int type) {
        img = new BufferedImage(x, y, type);
        scroll = new Vector2(0, 0);
        resized = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) img.getGraphics();
    }

    /**
     * Convert a position on a screen to a position on a {@code SurfaceMap}
     * based on what was calculated in {@link #getScroll}
     * <p>
     * NOTE: THIS METHOD DEPENDS ON {@link #getScroll}
     *
     * @param pos Position on the screen
     * @return Position on the SurfaceMap
     */
    public Vector2 getPos(Vector2 pos) {
        return new Vector2((pos.x - scroll.x) * img.getWidth() / resized.getWidth(),
                (pos.y - scroll.y) * img.getHeight() / resized.getHeight());
    }

    /**
     * Fill the entire buffered image with the color.
     *
     * @param color Color to fill
     */
    public void fill(Color color) {
        g2d.setColor(color);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Clear the image. Simply creates a new BufferedImage with the same dimensions and type.
     */
    public void clear() {
        g2d.dispose();
        img = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        reloadGraphics();
    }

    /**
     * Return the image's graphics if {@link #g2d} is null, otherwise {@link #g2d}
     *
     * @return Graphics2D
     */
    public Graphics2D getGraphics() {
        if (g2d == null) {
            return (Graphics2D) img.getGraphics();
        }
        return g2d;
    }

    /**
     * Reload the graphics. Set the {@link #g2d} to {@code {@link #img}.getGraphics()}
     */
    public void reloadGraphics() {
        g2d = (Graphics2D) img.getGraphics();
    }

    /**
     * Convert a position the {@code SurfaceMap} to a position on a screen
     * based on what was calculated in {@link #getScroll}
     * <p>
     * NOTE: THIS METHOD DEPENDS ON {@link #getScroll}
     *
     * @param pos Position on the {@code SurfaceMap}
     * @return Position on the screen
     */
    public Vector2 fromPos(Vector2 pos) {
        return new Vector2(pos.x * resized.getWidth() / img.getWidth() + scroll.x,
                pos.y * resized.getHeight() / img.getHeight() + scroll.y);
    }

    /**
     * Get resized BufferedImage relative to dimensions.
     *
     * @param dim         Size of the screen
     * @param multipliers Multipliers (multiplied by the size of the screen to get the size of the new image)
     * @return BufferedImage
     */
    public BufferedImage getResized(Dimension dim, double[] multipliers) {
        int newWidth = multipliers[0] != 0 ? Double.valueOf(dim.width * multipliers[0]).intValue() : dim.width;
        int newHeight = multipliers[1] != 0 ? Double.valueOf(dim.height * multipliers[1]).intValue() : dim.height;

        resized = new BufferedImage(newWidth, newHeight, img.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHints(Constants.RENDER_HINTS);
        g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(),
                img.getHeight(), null);
        g.dispose();
        return resized;
    }

    /**
     * Blit the SurfaceMap to the screen.
     *
     * NOTE: THIS METHOD DEPENDS ON {@link #getResized}
     *
     * @param screen Graphics to blit to
     */
    public void blitTo(Graphics2D screen) {
        screen.drawImage(resized, (int) scroll.x, (int) scroll.y, null);
    }


    /**
     * Calculate where to blit the {@code SurfaceMap} on {@code screen}
     * centering {@code target} at {@code center}.
     * <p>
     * NOTE: THIS METHOD DEPENDS ON {@link #getResized}
     *
     * @param target Position to scroll to on the SurfaceMap
     * @param screen Dimensions of the screen
     * @param center Where to center {@code target} on the screen
     * @param lockX  Lock the x coordinate of {@code target} to {@code center} on the {@code screen}
     * @param lockY  Lock the y coordinate of {@code target} to {@code center} on the {@code screen}
     * @return Position to blit the {@code SurfaceMap} at.
     */
    public Vector2 getScroll(Vector2 target, Dimension screen, Vector2 center, boolean lockX, boolean lockY) {
        scroll = new Vector2(-(target.x * resized.getWidth() / img.getWidth()) + center.x,
                -(target.y * resized.getHeight() / img.getHeight()) + center.y);
        scroll.x = Math.max(-(resized.getWidth() - screen.getWidth()), Math.min(0, scroll.x));
        scroll.y = Math.max(-(resized.getHeight() - screen.getHeight()), Math.min(0, scroll.y));

        scroll.x = lockX ? scroll.x : 0;
        scroll.y = lockY ? scroll.y : 0;

        return scroll;
    }
}
