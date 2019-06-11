package com.rotartsi.untitledgame.bullets;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.BulletFactory;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;
import com.rotartsi.jgame.sprite.GroupCollection;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.untitledgame.states.TestingState;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Bullets {
    public static class TestBulletFactory extends BulletFactory {
        public static BufferedImage publicImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        private TestingState game;

        public TestBulletFactory(TestingState game, ScreenBounds bounds) {
            super();
            groups = new GroupCollection();
            groups.add(game.bullets);
            img = publicImage;
            this.bounds = bounds;
            System.out.println("bounds.maxCoords = " + Arrays.toString(bounds.maxCoords));
            System.out.println("bounds.minCoords = " + Arrays.toString(bounds.minCoords));
            speed = 1200;
            life = 999999999;
            blume = new Vector2(200, 200);
            // minBlume = new Vector2(100, 100);
//            maxBlume = new Vector2(1000, 1000);
            minBlume = new Vector2(1, 1);
            maxBlume = new Vector2(2, 2);
            blumePerShot = new Vector2(25, 25);
            this.game = game;
            blumeDecrease = new Vector2(10, 10);
        }

        @Override
        public void addBullet(Vector2 target, double damage, PlatformerPlayer owner) {
            CustomBulletClass np = new CustomBulletClass(this.img, game.player.bounds, target, owner.pos, this.speed, this.life, this.blume);
            np.setBulletAttributes(0, owner, damage);
            np.collidables = new GroupCollection();
            np.collidables.add(game.obstacles);
            np.collidables.add(game.playerGroup);
            np.collidables.add(game.enemies);
            game.bullets.add(np);
//            System.out.println("game.bullets.sprites.size() = " + game.bullets.sprites.size());
        }

        @Override
        public void tick() {
            super.tick();
//            System.out.println("blume = " + blume);
        }
    }
}
