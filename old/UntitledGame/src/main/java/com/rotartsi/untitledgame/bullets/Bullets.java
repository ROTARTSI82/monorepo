package com.rotartsi.untitledgame.bullets;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.BulletFactory;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.untitledgame.states.TestingState;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Bullets {
    public static class TestBulletFactory extends BulletFactory {
        public static BufferedImage publicImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        private TestingState game;

        public TestBulletFactory(TestingState game) {
            super();
            group = game.bullets;
            img = publicImage;
            bounds = new ScreenBounds(new Rectangle2D.Double(0, 0, game.map.img.getWidth(), game.map.img.getHeight()),
                    game.dummyObstacle, game.dummyObstacle, game.dummyObstacle, game.dummyObstacle);
            System.out.println("bounds.maxCoords = " + Arrays.toString(bounds.maxCoords));
            System.out.println("bounds.minCoords = " + Arrays.toString(bounds.minCoords));
            speed = 1200;
            life = 999999999;
            blume = new Vector2(2, 2);
            minBlume = new Vector2(0.25, 0.25);
            maxBlume = new Vector2(3, 3);
            blumePerShot = new Vector2(0.25, 0.25);
            this.game = game;
            blumeDecrease = new Vector2(10, 10);
        }

        @Override
        public void addBullet(Vector2 target, double damage, PlatformerPlayer owner) {
            CustomBulletClass np = new CustomBulletClass(this.img, this.bounds, game.map.getPos(target), owner.pos, this.speed, this.life, this.blume);
            np.setBulletAttributes(0, owner, damage);
            game.bullets.add(np);
//            System.out.println("game.bullets.sprites.size() = " + game.bullets.sprites.size());
        }
    }
}
