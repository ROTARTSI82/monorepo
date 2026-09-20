package com.rotartsi.jgame.sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpriteSheet {
    private BufferedImage rawSheet;
    private Dimension spriteSize;

    public SpriteSheet(BufferedImage sheet, Dimension spriteSize) {
        this.spriteSize = spriteSize;
        this.rawSheet = sheet;
    }

    public BufferedImage getSprite(Dimension location) {
        Dimension pixelLocation = new Dimension(location.width * spriteSize.width,
                location.height * spriteSize.height);
        Rectangle area = new Rectangle(pixelLocation.width, pixelLocation.height,
                spriteSize.width, spriteSize.height);
        return getSprite(area);
    }

    public BufferedImage getSprite(Rectangle area) {
        BufferedImage ret = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) ret.getGraphics();
        g2d.drawImage(rawSheet, -area.x, -area.y, null);
        g2d.dispose();
        return ret;
    }
}
