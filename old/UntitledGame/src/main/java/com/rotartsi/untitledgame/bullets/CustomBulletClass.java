package com.rotartsi.untitledgame.bullets;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.PlatformerProjectile;
import com.rotartsi.jgame.sprite.Sprite;
import com.rotartsi.jgame.util.ScreenBounds;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class CustomBulletClass extends PlatformerProjectile {
    public CustomBulletClass(BufferedImage img, ScreenBounds bounds, Vector2 target, Vector2 position, double speed, long life, Vector2 blume) {
        super(img, bounds, target, position, speed, life, blume);
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        this.frameRateMult = (double) (now - this.lastUpdate) / 1000.0D;
//        this.checkBounds();
        LinkedList<Sprite> cols = this.collidesWith(collidables);
        if (cols.size() > 0) {
            this.onCollide(cols, "?");
        }

        this.vel = this.vel.add(new Vector2((double) (Constants.rand.nextBoolean() ? 1 : -1) * Constants.rand.nextDouble() * this.wobble.x * this.frameRateMult, (double) (Constants.rand.nextBoolean() ? 1 : -1) * Constants.rand.nextDouble() * this.wobble.y * this.frameRateMult));
        this.pos = this.pos.add(this.vel.multiply(new Vector2(this.frameRateMult, this.frameRateMult)));
        this.updateRect();
        if (now - this.born >= this.projectileLife) {
            this.requestKill();
        }

        this.lastUpdate = now;
    }
}
