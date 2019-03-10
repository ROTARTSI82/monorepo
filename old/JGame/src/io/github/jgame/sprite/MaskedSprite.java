package io.github.jgame.sprite;

import io.github.jgame.math.PolygonCollide;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class MaskedSprite extends Sprite {
    private PolygonCollide hitbox;
    private Polygon poly;

    public MaskedSprite(BufferedImage img, PolygonCollide hitbox) {
        super(img);
        this.hitbox = hitbox;
    }

    @Override
    public void updateRect() {
        super.updateRect();
        hitbox.restore();
        hitbox.moveTo(pos);
        //hitbox.scaleTo(zoom);
        //hitbox.rotate(rot);
        poly = hitbox.getPolygon();
    }

    @Override
    public void blitRect(Graphics2D screen) {
        screen.draw(poly);
    }


    public LinkedList<Sprite> collidesWith(Group sprites) {
        LinkedList<Sprite> ret = new LinkedList<>();
        for (Sprite s : sprites.sprites) {
            if (s instanceof MaskedSprite) {
                if (((MaskedSprite) s).hitbox.intersects(hitbox)) {
                    ret.add(s);
                }
            } else if (hitbox.intersects(s.rect)) {
                ret.add(s);
            }
        }
        return ret;
    }
}
