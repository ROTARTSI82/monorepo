package io.github.jgame.sprite;

import io.github.jgame.math.PolygonCollide;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * A sprite using a {@link PolygonCollide} for collision detection rather than a traditional
 * {@link Rectangle}
 */
public class MaskedSprite extends Sprite {
    private PolygonCollide hitbox;
    private Polygon poly;

    /**
     * New masked sprite!
     *
     * @param img    Image the sprite should display
     * @param hitbox The hitbox used for collision detection.
     */
    public MaskedSprite(BufferedImage img, PolygonCollide hitbox) {
        super(img);
        this.hitbox = hitbox;
    }

    /**
     * Update the hitbox and move it to the current position.
     */
    @Override
    public void updateRect() {
        super.updateRect();
        hitbox.restore();
        hitbox.moveTo(pos);
        //hitbox.scaleTo(zoom);
        //hitbox.rotate(rot);
        poly = hitbox.getPolygon();
    }

    /**
     * Draw the hitbox to the screen using {@link PolygonCollide}.getPolygon() and blitting that.
     *
     * @param screen The graphics to blit the hitbox to.
     */
    @Override
    public void blitRect(Graphics2D screen) {
        screen.draw(poly);
    }


    /**
     * Check if this sprite is touching any other sprites
     *
     * @param sprites The Group to check collisions with.
     * @return A list of sprites this is colliding with.
     */
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
