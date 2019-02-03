package io.github.jgame.tests;

import io.github.jgame.math.Vector2;
import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.util.Random;

public class Enemy extends Sprite {
    Random rand = new Random();

    public Enemy(int x, int y, Image img) {
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
