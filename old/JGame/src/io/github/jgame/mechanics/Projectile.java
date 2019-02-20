package io.github.jgame.mechanics;

import io.github.jgame.math.Vector2;
import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Projectile extends Sprite {
    private Vector2 myTarget;
    private double mySpeed;

    private int projectileLife;
    private long born;

    public Projectile(BufferedImage img, Vector2 target, Vector2 position, double speed, int life) {
        super(img);
        pos = position;

        projectileLife = life;
        born = System.currentTimeMillis();

        rot = pos.angleTo(target);
        vel = pos.velocityTo(target, speed);
        myTarget = target;
        mySpeed = speed;
    }

    /**
     * Override this function if the projectile could only be killed in certain conditions.
     */
    public void requestKill() {
        kill();
    }

    public void recalculate(boolean rotation, boolean velocity) {
        if (rotation) {
            rot = pos.angleTo(myTarget);
        }
        if (velocity) {
            vel = pos.velocityTo(myTarget, mySpeed);
        }
    }

    @Override
    public void update() {
        pos = pos.add(vel);
        updateRect();

        if (System.currentTimeMillis() - born >= projectileLife) {
            requestKill();
        }
    }

    @Override
    public void blit(Graphics2D screen) {
        blitTo(screen);
    }

}
