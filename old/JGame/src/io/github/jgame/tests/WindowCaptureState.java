package io.github.jgame.tests;

import io.github.jgame.Constants;
import io.github.jgame.event.EventRobot;
import io.github.jgame.game.Game;
import io.github.jgame.game.State;
import io.github.jgame.math.Vector2;
import io.github.jgame.sprite.Sprite;

import java.awt.*;

public class WindowCaptureState extends State {
    private EventRobot robot;
    private Rectangle area;

    public WindowCaptureState(Game game) {
        super(game);
        area = new Rectangle(0, 0, 1440, 900);
        try {
            robot = new EventRobot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(Constants.RENDER_HINTS);
        Sprite sprite = new Sprite(robot.createScreenCapture(area));
        Dimension d = game.runner.getSize();
        sprite.pos = new Vector2(d.width / 2d, d.height / 2d);
        sprite.updateRect();
        sprite.blitRotozoomTo(0, new double[]{d.width, d.height}, g2d);
        g2d.dispose();
    }
}
