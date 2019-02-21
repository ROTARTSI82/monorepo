package io.github.jgame.gui;

import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ProgressBar extends Sprite {
    public double maxVal;
    public double minVal;
    public double value;
    private Rectangle outline;
    private Rectangle bar;
    private Dimension innerSize;
    private int width;
    private Color barColor;
    private Color outlineColor;

    public ProgressBar(double min, double max, double val, Dimension barSize, int outlineWidth,
                       Color insideColor, Color outsideColor) {
        super(new BufferedImage((int) barSize.getWidth() + outlineWidth * 2,
                (int) barSize.getHeight() + outlineWidth * 2, BufferedImage.TYPE_INT_ARGB));
        minVal = min;
        maxVal = max;
        value = val;
        innerSize = barSize;
        width = outlineWidth;
        barColor = insideColor;
        outlineColor = outsideColor;
        updateBar();
    }

    public void updateBar() {
        value = Math.max(minVal, Math.min(value, maxVal));  // Clamp value.
        bar = new Rectangle(width, width,
                (int) ((value - minVal) * (innerSize.width / (maxVal - minVal))), innerSize.height);
        outline = new Rectangle(0, 0, innerSize.width + width * 2, innerSize.height + width * 2);
    }

    @Override
    public void blit(Graphics2D screen) {
        screen.setColor(outlineColor);
        screen.fillRect(outline.x + (int) absPos.x, outline.y + (int) absPos.y, outline.width, outline.height);

        screen.setColor(barColor);
        screen.fillRect(bar.x + (int) absPos.x, bar.y + (int) absPos.y, bar.width, bar.height);
    }
}
