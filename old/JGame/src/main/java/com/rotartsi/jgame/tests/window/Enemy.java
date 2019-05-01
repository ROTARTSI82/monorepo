package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Sprite;

import java.awt.image.BufferedImage;

import static com.rotartsi.jgame.Constants.rand;

/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */
/**
 * @deprecated Only use for blitting tests.
 */
@Deprecated
public class Enemy extends Sprite {

    public Enemy(int x, int y, BufferedImage img) {
        super(img);
        pos = new Vector2(x, y);
        updateRect();
    }

    @Override
    public void update() {
        vel = new Vector2(rand.nextInt(3) - 1, rand.nextInt(3) - 1);
        pos = pos.add(vel);
        updateRect();
    }
}
