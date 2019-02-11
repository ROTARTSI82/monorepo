package io.github.jgame.tests.window;

import io.github.jgame.math.Vector2;
import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BulletSprite extends Sprite {
    private double rot;
    private long life;
    private Vector2 target;
    private boolean rotInit = false;

    public BulletSprite(Vector2 myTarget, Vector2 myPos, BufferedImage img) {
        super(img);
        pos = myPos;
        updateRect();
        life = System.currentTimeMillis();
        target = myTarget;
    }

    @Override
    public void blit(Graphics2D screen) {
        blitRotozoom(rot + 180, new double[]{1, 1}, screen);
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() - life > 10000) {
            kill();
        }
        if (!rotInit) {
            rot = pos.angleTo(target);
            vel = pos.velocityTo(target, 1);
            rotInit = true;
        }
        pos = pos.add(vel);
        updateRect();
    }
}
