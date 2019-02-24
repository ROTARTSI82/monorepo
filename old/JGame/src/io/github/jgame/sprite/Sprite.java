package io.github.jgame.sprite;

import io.github.jgame.math.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Sprite {
    public boolean visible, active;
    public boolean flipVertical, flipHorizontal;
    public Vector2 pos, vel;
    public double rot = 0;
    protected Vector2 absPos;
    Rectangle rect;
    BufferedImage image;
    LinkedList<Group> groups = new LinkedList<>();
    public double[] zoom = {1, 1};
    Vector2 size;

    public Sprite(BufferedImage spriteImage) {
        visible = true;
        active = true;
        image = spriteImage;
        size = new Vector2(image.getWidth(null), image.getHeight(null));
        rect = new Rectangle(0, 0, (int) size.x, (int) size.y);
        pos = new Vector2(0, 0);
        absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
        vel = new Vector2(0, 0);
    }

    public void updateRect() {
        absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
        rect = new Rectangle((int) absPos.x, (int) absPos.y, (int) size.x, (int) size.y);
    }

    public void blit(Graphics2D screen) {
    }

    public void updateImage(BufferedImage img) {
        image = img;
        size = new Vector2(img.getWidth(), img.getHeight());
        updateRect();
    }

    public void blitTo(Graphics2D screen) {
        BufferedImage blitImg = image;
        if (flipHorizontal) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-blitImg.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            blitImg = op.filter(blitImg, null);
        }

        AffineTransform id = AffineTransform.getTranslateInstance(this.absPos.x, this.absPos.y);
        AffineTransform trans = AffineTransform.getTranslateInstance(this.absPos.x, this.absPos.y);
        trans.setTransform(id);

        Vector2 center = new Vector2(this.image.getWidth(null) / 2d,
                this.image.getHeight(null) / 2d);
        trans.translate(center.x * zoom[0], center.y * zoom[1]);
        trans.rotate(Math.toRadians(rot + (flipVertical ? 180 : 0)));
        trans.scale(zoom[0], zoom[1]);
        size.x = center.x * 2 * zoom[0];
        size.y = center.y * 2 * zoom[1];
        updateRect();
        trans.translate(-center.x, -center.y);
        screen.drawImage(blitImg, trans, null);
    }

    public void updateHover(boolean isHovering, Vector2 mousePos) {

    }

    public void onClick(Vector2 mousePos) {

    }

    public void zoomTo(double[] z) {
        Vector2 dim = new Vector2(this.image.getWidth(null), this.image.getHeight(null));
        zoom = new double[]{z[0] / dim.x, z[1] / dim.y};
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
