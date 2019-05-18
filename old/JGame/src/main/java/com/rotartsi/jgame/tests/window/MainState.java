package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.event.AnyEvent;
import com.rotartsi.jgame.game.Game;
import com.rotartsi.jgame.game.State;
import com.rotartsi.jgame.image.ImageManager;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mixer.Sound;
import com.rotartsi.jgame.mixer.SoundManager;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.ShapeSprite;
import com.rotartsi.jgame.sprite.Sprite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;

import static com.rotartsi.jgame.Constants.rand;

/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */
/**
 * @deprecated Use SpriteTest. Only useful for testing blitting.
 */
@Deprecated
public class MainState extends State {
    private final Logger logger = LogManager.getLogger(MainState.class);
    private final boolean displayRects = true;

    private Sprite player;
    private Group enemies;
    private Group bullets;

    private SoundManager soundManager = new SoundManager();
    private ImageManager imageLoader = new ImageManager();

    MainState(Game game) {
        super(game);
        logger.info("env: " + System.getenv());
        imageLoader.fromFile("assets/sprite.png", "sprite");
        imageLoader.fromFile("assets/bullet.png", "bullet");
        int size = 32;
        // Here for testing purposes
        Polygon testShape = new Polygon(new int[]{0, size, 2 * size}, new int[]{0, 2 * size, 0}, 3);
        //player = new TextSprite("HelloWorld!",
        //        new Font("Arial", Font.PLAIN, 50), Color.BLACK);
        //player = new Sprite(ImageManager.fromShape(testShape, Color.BLACK));
        //player = new Sprite(imageLoader.get("sprite"));
        //player.zoomTo(new double[]{64, 64});
        player = new ShapeSprite(testShape, 1);
        player.zoomTo(new double[]{64, 64});
        enemies = new Group();
        bullets = new Group();
        Enemy enemy = new Enemy(640, 480, imageLoader.get("sprite"));
        enemy.zoomTo(new double[]{64, 64});
        enemies.add(enemy);
        soundManager.addSound("fireball", new Sound("assets/fireball.wav"));
    }

    @Override
    public MouseHandler[] getMouseHandlers() {
        return new MouseHandler[]{new MouseHandler()};
    }

    @Override
    public KeyHandler[] getKeyHandlers() {
        return new KeyHandler[]{new KeyHandler()};
    }

    @Override
    public void updateGraphics(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1440, 900);
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        if (player.collidesWith(enemies).size() >= 1) {
            System.out.println("h");
        }
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        if (displayRects) {
            player.blitRect(g2d);
        }
        player.rot = player.pos.angleTo(mouseHandlers[0].mouseSprite.pos) + 90;
        player.blitTo(g2d);
        for (Sprite enemy : enemies.sprites) {
            enemy.update();
            if (displayRects) {
                enemy.blitRect(g2d);
            }
            enemy.rot = enemy.pos.angleTo(player.pos) - 90;
            enemy.blitTo(g2d);
        }
        for (Object bullet : (LinkedList) bullets.sprites.clone()) {
            if (bullet instanceof BulletSprite) {
                BulletSprite bsprite = (BulletSprite) bullet;
                for (Sprite col : bsprite.collidesWith(enemies)) {
                    col.kill();
                    for (int i = 0; i < 2; i++) {
                        Enemy enemy = new Enemy(rand.nextInt(1440), rand.nextInt(900),
                                imageLoader.get("sprite"));
                        enemy.zoomTo(new double[]{64, 64});
                        enemies.add(enemy);
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
        player.flipHorizontal = true;
        player.flipVertical = true;
        player.pos = player.pos.add(player.vel);
        player.updateRect();
    }

    public class KeyHandler extends State.KeyHandler {
        @Override
        public void keyPressed(KeyEvent e) {
            //System.out.println(e.paramString());
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
            //System.out.println(e.paramString());
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

    @Override
    public void onEvent(AnyEvent event) {
        System.out.println(event);/*
        if (event.matchesString("id:key.pressed,mods:64,keyCode:16")) {
            System.out.println("YAYA!");
        }*/
    }

    public class MouseHandler extends State.MouseHandler {
        volatile Vector2 pos = new Vector2(0, 0);

        @Override
        public void mouseMoved(MouseEvent e) {
            //System.out.println(e.paramString());
            mouseHandlers[0].mouseSprite.pos.x = e.getX();
            mouseHandlers[0].mouseSprite.pos.y = e.getY();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            //System.out.println(e.paramString());
            //System.out.println(e.getUnitsToScroll());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //System.out.println(e.paramString());
            bullets.add(new BulletSprite(mouseHandlers[0].mouseSprite.pos, player.pos, imageLoader.get("bullet")));
            soundManager.play("fireball", true, 0.5f);
            System.out.println(soundManager.get("fireball").getVal(FloatControl.Type.MASTER_GAIN));
        }
    }
}
