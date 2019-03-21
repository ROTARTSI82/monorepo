package io.github.jgame.gui;

import io.github.jgame.sprite.Sprite;

import java.awt.*;
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
    private Rectangle outline;

    /**
     * The dimensions of the actual bar.
     */
    private Rectangle bar;

    /**
     * The size of a full bar.
     */
    private Dimension innerSize;

    /**
     * The width of the outline around the bar.
     */
    private int width;

    /**
     * The color of the bar.
     */
    private Color barColor;

    /**
     * The color of the area outlining the bar
     */
    private Color outlineColor;

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
     */
    public ProgressBar(double min, double max, double val, Dimension barSize, int outlineWidth,
                       Color insideColor, Color outsideColor, Color emptyBarColor) {
        super(new BufferedImage((int) barSize.getWidth() + outlineWidth * 2,
                (int) barSize.getHeight() + outlineWidth * 2, BufferedImage.TYPE_INT_ARGB));
        minVal = min;
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
        bar = new Rectangle(width, width,
                (int) ((value - minVal) * (innerSize.width / (maxVal - minVal))), innerSize.height);
        outline = new Rectangle(0, 0, innerSize.width + width * 2, innerSize.height + width * 2);
    }

    /**
     * Draw the ProgressBar to the screen!
     *
     * @param screen Graphics to blit to
     */
    @Override
    public void blit(Graphics2D screen) {
        screen.setColor(outlineColor);
        screen.fillRect(outline.x + (int) absPos.x, outline.y + (int) absPos.y, outline.width, outline.height);

        screen.setColor(emptyColor);
        screen.fillRect(width + (int) absPos.x, width + (int) absPos.y, innerSize.width, innerSize.height);

        screen.setColor(barColor);
        screen.fillRect(bar.x + (int) absPos.x, bar.y + (int) absPos.y, bar.width, bar.height);
    }
}
