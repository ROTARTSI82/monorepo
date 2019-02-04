package io.github.jgame.sprite;

import io.github.jgame.math.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

public class Sprite {
    public Rectangle rect;
    public Image image;
    public Shape shape;
    public SpriteType type;
    public double multiplier;
    public Vector2 pos, absPos, size, vel;
    LinkedList<Group> groups = new LinkedList<>();

    public Sprite(Image spriteImage) {
        image = spriteImage;
        size = new Vector2(image.getWidth(null), image.getHeight(null));
        rect = new Rectangle(100, 100, (int) size.x, (int) size.y);
        pos = new Vector2(100, 100);
        absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
        vel = new Vector2(0, 0);
        type = SpriteType.IMAGE;
    }

    /**
     * @param spriteImage      Could be either Ellipse2D or Polygon
     * @param spriteMultiplier Use 2 for Ellipse2D and 1 for Polygon since circles are weird.
     */
    public Sprite(Shape spriteImage, double spriteMultiplier) {
        type = SpriteType.SHAPE;
        multiplier = spriteMultiplier;
        shape = spriteImage;
        rect = shape.getBounds();
        vel = new Vector2(0, 0);
        rect = shape.getBounds();
        size = new Vector2(rect.width, rect.height);
        absPos = new Vector2(rect.x, rect.y);
        pos = absPos.add(new Vector2(size.x / 2, size.y / 2));
    }

    public void updateRect() {
        switch (type) {
            case IMAGE: {
                absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
                rect = new Rectangle((int) absPos.x, (int) absPos.y, (int) size.x, (int) size.y);
                break;
            }
            case SHAPE: {
                rect = shape.getBounds();
                size = new Vector2(rect.width, rect.height);
                absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
                rect.x = (int) absPos.x;
                rect.y = (int) absPos.y;
                break;
            }
        }
    }

    public void blit(Graphics2D screen) {
    }

    public void blitRotozoom(double rot, double[] zoom, Graphics2D screen) {
        AffineTransform id = AffineTransform.getTranslateInstance(this.absPos.x, this.absPos.y);
        AffineTransform trans = AffineTransform.getTranslateInstance(this.absPos.x, this.absPos.y);
        trans.setTransform(id);
        Vector2 center = new Vector2(0, 0);
        switch (type) {
            case IMAGE: {
                center = new Vector2(this.image.getWidth(null) / 2d,
                        this.image.getHeight(null) / 2d);
                break;
            }
            case SHAPE: {
                Rectangle tmpRect = shape.getBounds();
                center = new Vector2(tmpRect.width / 2d, tmpRect.height / 2d);
                id = AffineTransform.getTranslateInstance(rect.x, rect.y);
                trans = AffineTransform.getTranslateInstance(rect.x, rect.y);
                trans.setTransform(id);
                break;
            }
        }
        trans.translate(center.x, center.y);
        trans.rotate(Math.toRadians(rot));
        trans.scale(zoom[0], zoom[1]);
        size.x = center.x * 2 * zoom[0];
        size.y = center.y * 2 * zoom[1];
        updateRect();
        switch (type) {
            case IMAGE: {
                trans.translate(-center.x, -center.y);
                screen.drawImage(this.image, trans, null);
                break;
            }
            case SHAPE: {
                trans.translate(-center.x * multiplier, -center.y * multiplier);
                screen.draw(trans.createTransformedShape(shape));
                break;
            }
        }
    }

    public void blitRotozoomTo(double rot, double[] zoom, Graphics2D screen) {
        double[] multiplier;
        Vector2 dim = new Vector2(0, 0);
        switch (type) {
            case IMAGE: {
                dim = new Vector2(this.image.getWidth(null), this.image.getHeight(null));
                break;
            }
            case SHAPE: {
                Rectangle tmpRect = shape.getBounds();
                dim = new Vector2(tmpRect.width, tmpRect.height);
                break;
            }
        }
        multiplier = new double[]{zoom[0] / dim.x, zoom[1] / dim.y};
        blitRotozoom(rot, multiplier, screen);
    }

    public enum SpriteType {
        IMAGE, SHAPE
    }

    public void blitRect(Graphics2D screen) {
        screen.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    public LinkedList<Sprite> collidesWith(Group sprites) {
        LinkedList<Sprite> ret = new LinkedList<>();
        for (Sprite s : sprites.sprites) {
            if (s.rect.intersects(rect)) {
                ret.add(s);
            }
        }
        return ret;
    }

    public void update() {
    }

    public void kill() {
        for (Object g : (LinkedList) groups.clone()) {
            if (g instanceof Group) {
                Group g2 = (Group) g;
                g2.remove(this);
            }
        }
    }
}
