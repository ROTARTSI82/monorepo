package com.rotartsi.jgame.sprite;

import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.math.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;

@Deprecated
public class ShapeSprite extends Sprite {
    private Shape shape;
    private double multiplier;

    public ShapeSprite(Shape spriteShape, double spriteMultiplier) {
        super(ImageManager.fromShape(spriteShape, Color.BLACK));
        shape = spriteShape;
        multiplier = spriteMultiplier;
        rect = shape.getBounds();
        vel = new Vector2(0, 0);
        rect = shape.getBounds();
        size = new Vector2(rect.width, rect.height);
        absPos = new Vector2(rect.x, rect.y);
        pos = absPos.add(new Vector2(size.x / 2, size.y / 2));
    }

    @Override
    public void updateRect() {
        rect = shape.getBounds();
        size = new Vector2(rect.width, rect.height);
        absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
        rect.x = (int) absPos.x;
        rect.y = (int) absPos.y;
    }

    @Override
    public void blitTo(Graphics2D g2d) {
        AffineTransform id = AffineTransform.getTranslateInstance(this.absPos.x, this.absPos.y);
        AffineTransform trans = AffineTransform.getTranslateInstance(this.absPos.x, this.absPos.y);
        trans.setTransform(id);

        Rectangle tmpRect = shape.getBounds();
        Vector2 center = new Vector2(tmpRect.width / 2d, tmpRect.height / 2d);
        id = AffineTransform.getTranslateInstance(rect.x, rect.y);
        trans = AffineTransform.getTranslateInstance(rect.x, rect.y);
        trans.setTransform(id);

        trans.translate(center.x * zoom[0], center.y * zoom[1]);
        trans.rotate(Math.toRadians(rot + (flipVertical ? 180 : 0)));
        trans.scale(zoom[0], zoom[1]);
        size.x = center.x * 2 * zoom[0];
        size.y = center.y * 2 * zoom[1];
        updateRect();

        trans.translate(-center.x * multiplier, -center.y * multiplier);
        g2d.draw(trans.createTransformedShape(shape));
    }

    @Override
    public void zoomTo(double[] z) {
        Rectangle tmpRect = shape.getBounds();
        Vector2 dim = new Vector2(tmpRect.width, tmpRect.height);

        zoom = new double[]{z[0] / dim.x, z[1] / dim.y};
    }
}
