package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.GroupCollection;
import com.rotartsi.jgame.util.ScreenBounds;

import java.awt.image.BufferedImage;

public class BulletFactory {
    public GroupCollection groups;
    public BufferedImage img;
    public ScreenBounds bounds;
    public double speed;
    public long life;
    public Vector2 blume;

    public Vector2 blumePerShot;
    public Vector2 maxBlume;
    public Vector2 minBlume;

    public Vector2 blumeDecrease;

    public long lastTick = System.currentTimeMillis();

    public BulletFactory() {
    }

    public void addBullet(Vector2 target, double damage, PlatformerPlayer owner) {
        PlatformerProjectile np = new PlatformerProjectile(img, bounds, target, owner.pos, speed, life, blume);
        np.setBulletAttributes(0, owner, damage);
        for (Group group : groups.groups) {
            group.add(np);
        }
    }

    public void tick() {
        long now = System.currentTimeMillis();
        double frameMult = (now - lastTick) / 1000d;

        blume = blume.subtract(new Vector2(blumeDecrease.x * frameMult, blumeDecrease.y * frameMult));

        blume = new Vector2(Math.max(Math.min(blume.x, maxBlume.x), minBlume.x),
                Math.max(Math.min(blume.y, maxBlume.y), minBlume.y));

        lastTick = now;
    }

    public void onFire() {
        blume = blume.add(blumePerShot); // Make blume more random
    }
}
