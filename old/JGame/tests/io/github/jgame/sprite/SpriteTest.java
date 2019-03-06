package io.github.jgame.sprite;

import io.github.jgame.math.Vector2;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.testng.Assert.assertEquals;

public class SpriteTest {
    private Sprite test;

    @BeforeSuite
    public void setUp() {
        test = new Sprite(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
    }

    @Test
    public void testUpdateRect() {
        test.pos = new Vector2(150, 150);
        test.updateRect();
        assertEquals(test.absPos.toInt(), new int[]{100, 100});
    }

    @Test
    public void testCollidesWith() {
        Sprite other = new Sprite(new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB));
        Group group = new Group();
        group.add(test);

        other.pos = new Vector2(100, 100);
        test.pos = new Vector2(150, 150);
        test.updateRect();
        other.updateRect();

        assertEquals(other.collidesWith(group).size(), 1);
    }

    /**
     * @deprecated ShapeSprite() is depreciated. Use {@code Sprite(ImageManager.fromShape(myShape))} instead
     */
    @Test
    @Deprecated
    public void testShapeSprite() {
        Polygon testShape = new Polygon(new int[]{16, 0, 32}, new int[]{0, 32, 32}, 3);
        ShapeSprite test = new ShapeSprite(testShape, 1);

        assertEquals(test.size.x, 32);
        assertEquals(test.size.y, 32);
    }

    @Test
    public void testTextSprite() {
        TextSprite testText = new TextSprite("Hello World!", new Font("Arial", Font.PLAIN, 50),
                Color.BLACK);
    }

    @Test
    public void testKill() {
        Group group = new Group();
        group.add(test);

        test.kill();
        assertEquals(test.groups.size(), 0);
        assertEquals(group.sprites.size(), 0);
    }
}