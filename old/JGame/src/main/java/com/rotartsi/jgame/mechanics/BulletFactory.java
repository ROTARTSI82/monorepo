package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.util.ScreenBounds;

import java.awt.image.BufferedImage;

public class BulletFactory {
    Group group;
    BufferedImage img;
    ScreenBounds bounds;
    double speed;
    long life;
    Vector2 blume;

    public BulletFactory() {
    }

    public void addBullet(Vector2 target, double damage, PlatformerEntity owner) {
        PlatformerProjectile np = new PlatformerProjectile(img, bounds, target, owner.pos, speed, life, blume);
        np.setBulletAttributes(0, owner, damage);
        group.add(np);
    }
}
