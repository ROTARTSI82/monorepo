package com.rotartsi.jgame.sprite;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.gui.ButtonManager;
import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.math.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static com.rotartsi.jgame.Constants.JGameStr;

/**
 * Sprites! Support for translating, collision detection, rotating, scaling, and reflection!
 */
public class Sprite {

    /**
     * The placeholder image to use in case the image passed to the constructor is null.
     */
    public static BufferedImage defaultImage = ImageManager.fromText("MissIMG", Constants.defaultFont, Color.black);

    /**
     * Determines if {@code ButtonManager.blit(Graphics2D g2d)} also draws
     * this sprite. See {@link ButtonManager}
     */
    public boolean visible;
    /**
     * {@code active} determines whether this sprite is acting as a button ({@code ButtonManager.update()} would call
     * this sprite's {@link #updateHover(boolean, Vector2)} and {@link #onClick(Vector2)}
     */
    public boolean active;
    /**
     * Vertical mirroring/reflection
     */
    public boolean flipVertical;
    /**
     * Horizontal mirroring/reflection
     */
    public boolean flipHorizontal;
    /**
     * The position of the sprite.
     */
    public Vector2 pos;
    /**
     * The velocity of the sprite.
     */
    public Vector2 vel;
    public Vector2 rectPos;
    public Vector2 rectSize;
    /**
     * The rotation of the sprite.
     */
    public double rot = 0;
    /**
     * The rectangle used for collision detection by the sprite.
     */
    Rectangle2D.Double rect;

    /**
     * The absolute position to blit this image to. (the topleft of the image.)
     */
    public Vector2 absPos;
    /**
     * The sprite's image.
     */
    BufferedImage image;
    /**
     * Logger object used for logging.
     */
    private Logger logger = LogManager.getLogger(Sprite.class);

    /**
     * A list of groups this sprite is in.
     */
    LinkedList<Group> groups = new LinkedList<>();
    double[] zoom = {1, 1};

    /**
     * The dimensions of the image.
     */
    public Vector2 size;

    /**
     * Create a new sprite!
     *
     * @param spriteImage Image.
     */
    public Sprite(BufferedImage spriteImage) {
        if (spriteImage == null) {
            logger.warn(JGameStr.getString("sprite.Sprite.nullImage"));
            image = ImageManager.deepCopy(defaultImage);
        } else {
            image = spriteImage;
        }

        visible = true;
        active = true;
        size = new Vector2(image.getWidth(null), image.getHeight(null));
        rect = new Rectangle2D.Double(0, 0, size.x, size.y);
        pos = new Vector2(0, 0);
        absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
        vel = new Vector2(0, 0);
        rectPos = new Vector2(0, 0);
        rectSize = new Vector2(size);
    }

    /**
     * Update {@link #absPos} and {@link #rect} according to {@link #pos} and {@link #size}.
     */
    public void updateRect() {
        //absPos = pos.subtract(new Vector2(size.x / 2d, size.y / 2d));
        //rect = new Rectangle2D.Double(absPos.x, absPos.y, size.x, size.y);
        absPos = pos.subtract(new Vector2(size.x / 2d, size.y / 2d));
        onRectUpdate();
        rect = new Rectangle2D.Double(rectPos.x, rectPos.y, rectSize.x, rectSize.y);
    }

    /**
     * Override this function to define a custom rectangle.
     */
    public void onRectUpdate() {
        rectPos = new Vector2(absPos);
        rectSize = new Vector2(size);
    }

    /**
     * Blit routine. Override this. Blank by default.
     *
     * @param screen Screen to blit to.
     */
    public void blit(Graphics2D screen) {
    }

    /**
     * Update the current image.
     *
     * @param img New image
     */
    public void updateImage(BufferedImage img) {
        image = img;
        size = new Vector2(img.getWidth(), img.getHeight());
        updateRect();
    }

    /**
     * Blit the sprite to the screen at the correct position. Apply appropriate AffineTransformations defined
     * by {@link #rot}, {@link #zoom}, etc.
     *
     * @param screen Screen to blit to.
     */
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

    /**
     * Update the image of the sprite according to the mouse's state over the sprite.
     * See {@link ButtonManager}
     *
     * @param isHovering true if mouse is hovering over the sprite
     * @param mousePos The position of the mouse
     */
    public void updateHover(boolean isHovering, Vector2 mousePos) {

    }

    /**
     * What to do when the sprite is clicked. See {@link ButtonManager}
     *
     * @param mousePos Where the mouse is.
     */
    public void onClick(Vector2 mousePos) {

    }

    /**
     * Scale the image to specific dimensions.
     *
     * @param z Dimensions.
     */
    public void zoomTo(double[] z) {
        Vector2 dim = new Vector2(this.image.getWidth(null), this.image.getHeight(null));
        zoom = new double[]{z[0] / dim.x, z[1] / dim.y};
    }

    /**
     * Draw the hitbox of the sprite to the screen to visualize collisions.
     *
     * @param screen Screen to blit to.
     */
    public void blitRect(Graphics2D screen) {
        screen.drawRect((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
    }

    /**
     * Check if this sprite collides with any sprite in the group.
     *
     * @param sprites Group to check
     * @return List of sprites we are touching.
     */
    public LinkedList<Sprite> collidesWith(Group sprites) {
        LinkedList<Sprite> ret = new LinkedList<>();
        for (Sprite s : sprites.sprites) {
            if (s.rect.intersects(rect)) {
                ret.add(s);
            }
        }
        return ret;
    }

    /**
     * Logic update. Override this.
     */
    public void update() {
    }

    /**
     * Remove this sprite from all groups it's in, effectively removing any reference to it.
     */
    public void kill() {
        for (Object g : (LinkedList) groups.clone()) {
            if (g instanceof Group) {
                Group g2 = (Group) g;
                g2.remove(this);
            }
        }
    }
}
