package io.github.jgame.tests;

import io.github.jgame.game.State;
import io.github.jgame.image.ImageLoader;
import io.github.jgame.logging.GenericLogger;
import io.github.jgame.math.Vector2;
import io.github.jgame.mixer.Sound;
import io.github.jgame.sprite.Group;
import io.github.jgame.sprite.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainState extends State {
    private final Logger logger;
    private final boolean displayRects = false;

    Sprite player;
    Group enemies;
    Group bullets;
    Sound fire;
    Shape testShape;

    Random rand = new Random();
    ImageLoader imageLoader = new ImageLoader();
    HashMap<String, Image> images = new HashMap<>();

    public MainState() {
        super();
        GenericLogger.setup(Level.FINE, Level.FINE, Level.OFF);
        logger = Logger.getLogger(this.getClass().getName());
        logger.info("env: " + System.getenv());

        tryImageFromFile("assets/sprite.png", "sprite");
        tryImageFromFile("assets/bullet.png", "bullet");
        int size = 32;
        testShape = new Polygon(new int[]{0, size, 2 * size}, new int[]{0, 2 * size, 0}, 3);
        player = new Sprite(testShape, 1);  // Use 1 since it's a polygon.
        enemies = new Group();
        bullets = new Group();
        enemies.add(new Enemy(640, 480, images.get("sprite")));
        fire = new Sound("assets/fireball.wav", Sound.LoadModes.FROM_FILE);
    }

    public void tryImageFromFile(String img, String id) {
        try {
            images.put(id, imageLoader.fromFile(img));
        } catch (Exception e) {
            logger.warning(String.format("Failed to load %s:\n%s", img, GenericLogger.getStackTrace(e)));
        }
    }

    @Override
    public MouseHandler getMouseHandler() {
        return new MouseHandler();
    }

    @Override
    public KeyHandler getKeyHandler() {
        return new KeyHandler();
    }

    @Override
    public void updateGraphics(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1440, 900);
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (displayRects) {
            player.blitRect(g2d);
        }
        player.blitRotozoom(player.pos.angleTo(mouseHandler.pos), new double[]{1, 1}, g2d);
        for (Sprite enemy : enemies.sprites) {
            enemy.update();
            if (displayRects) {
                enemy.blitRect(g2d);
            }
            enemy.blitRotozoom(0, new double[]{1, 1}, g2d);
        }
        for (Object bullet : (LinkedList) bullets.sprites.clone()) {
            if (bullet instanceof BulletSprite) {
                BulletSprite bsprite = (BulletSprite) bullet;
                for (Sprite col : bsprite.collidesWith(enemies)) {
                    col.kill();
                    for (int i = 0; i < 2; i++) {
                        enemies.add(new Enemy(rand.nextInt(1440), rand.nextInt(900), images.get("sprite")));
                    }
                }
                bsprite.update();
                if (displayRects) {
                    bsprite.blitRect(g2d);
                }
                bsprite.blit(g2d);
            }
        }
    }

    @Override
    public void updateLogic() {
        player.pos = player.pos.add(player.vel);
        player.updateRect();
    }

    public class KeyHandler extends State.KeyHandler {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case (KeyEvent.VK_UP): {
                    player.vel.y = -1;
                    break;
                }
                case (KeyEvent.VK_DOWN): {
                    player.vel.y = 1;
                    break;
                }
                case (KeyEvent.VK_LEFT): {
                    player.vel.x = -1;
                    break;
                }
                case (KeyEvent.VK_RIGHT): {
                    player.vel.x = 1;
                    break;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case (KeyEvent.VK_UP):
                case (KeyEvent.VK_DOWN): {
                    player.vel.y = 0;
                    break;
                }
                case (KeyEvent.VK_LEFT):
                case (KeyEvent.VK_RIGHT): {
                    player.vel.x = 0;
                    break;
                }
            }
        }
    }

    public class MouseHandler extends State.MouseHandler {
        Vector2 pos = new Vector2(0, 0);

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseHandler.pos.x = e.getX();
            mouseHandler.pos.y = e.getY();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            bullets.add(new BulletSprite(mouseHandler.pos, player.pos, images.get("bullet")));
            fire.play(true);
        }
    }
}
