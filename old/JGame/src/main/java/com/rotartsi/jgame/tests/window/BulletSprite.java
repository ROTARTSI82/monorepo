package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */
/**
 * @deprecated Only use for blitting tests.
 */
@Deprecated
public class BulletSprite extends Sprite {
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
        blitTo(screen);
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
