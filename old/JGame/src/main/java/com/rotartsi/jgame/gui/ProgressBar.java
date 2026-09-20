package com.rotartsi.jgame.gui;

import com.rotartsi.jgame.sprite.Sprite;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Useful bar for displaying progress.
 * <p>
 * Could be useful as a Health bar too.
 * <p>
 * Extends {@link Sprite}
 */
public class ProgressBar extends Sprite {
    /**
     * The maximum value of {@link #value}. The value is clamped.
     */
    public double maxVal;

    /**
     * The minimum value of {@link #value}. The value is clamped.
     */
    public double minVal;

    /**
     * The actual value of the progress bar.
     */
    public double value;

    /**
     * Color of the bar when empty (the background of the bar)
     */
    private Color emptyColor;

    /**
     * The dimensions of the big rectangle outlining the entire progress bar
     */
    private Rectangle2D.Double outline;

    /**
     * The dimensions of the actual bar.
     */
    private Rectangle2D.Double bar;

    /**
     * The size of a full bar.
     */
    private Dimension innerSize;

    /**
     * The width of the outline around the bar.
     */
    private double width;

    /**
     * The color of the bar.
     */
    private Color barColor;

    /**
     * The color of the area outlining the bar
     */
    private Color outlineColor;

    private boolean isVert;

    /**
     * ProgressBars!
     *
     * @param min           Minimum value for the bar
     * @param max           Maximum value for the bar
     * @param val           Value for the bar
     * @param barSize       The bar size (not including the outline)
     * @param outlineWidth  The width of the outline
     * @param insideColor   Color of the bar
     * @param outsideColor  Color of the outline
     * @param emptyBarColor Color of the bar filler (essentially a full bar rendered behind the bar)
     * @param isVertical    If the bar fills up vertically or horizontally
     */
    public ProgressBar(double min, double max, double val, Dimension barSize, double outlineWidth,
                       Color insideColor, Color outsideColor, Color emptyBarColor, boolean isVertical) {
        super(new BufferedImage((int) (barSize.getWidth() + outlineWidth * 2),
                (int) (barSize.getHeight() + outlineWidth * 2), BufferedImage.TYPE_INT_ARGB));
        minVal = min;
        isVert = isVertical;
        maxVal = max;
        value = val;
        innerSize = barSize;
        width = outlineWidth;
        barColor = insideColor;
        outlineColor = outsideColor;
        emptyColor = emptyBarColor;
        updateBar();
    }

    /**
     * Update the width of the bar according to the {@code value} of the ProgressBar. The value is clamped according
     * to {@code minVal} and {@code maxVal}
     */
    public void updateBar() {
        value = Math.max(minVal, Math.min(value, maxVal));  // Clamp value.
        if (!isVert) {
            bar = new Rectangle2D.Double(width, width,  // The bar is offset by [width, width] from the outline rect.
                    (value - minVal) * (innerSize.width / (maxVal - minVal)), innerSize.height);
        } else {
            bar = new Rectangle2D.Double(width, width,  // The bar is offset by [width, width] from the outline rect.
                    innerSize.width, (value - minVal) * (innerSize.height / (maxVal - minVal)));
        }
        outline = new Rectangle2D.Double(0, 0, innerSize.width + width * 2, innerSize.height + width * 2);
    }

    /**
     * Draw the ProgressBar to the screen!
     *
     * @param screen Graphics to blit to
     */
    @Override
    public void blit(Graphics2D screen) {
        screen.setColor(outlineColor);
        screen.fillRect((int) (outline.x + absPos.x), (int) (outline.y + absPos.y), (int) outline.width, (int) outline.height);

        screen.setColor(emptyColor);
        screen.fillRect((int) (width + absPos.x), (int) (width + absPos.y), innerSize.width, innerSize.height);

        screen.setColor(barColor);
        screen.fillRect((int) (bar.x + absPos.x), (int) (bar.y + absPos.y), (int) bar.width, (int) bar.height);
    }
}
