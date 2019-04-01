package io.github.jgame.tests.window;

import io.github.jgame.game.Game;
import io.github.jgame.game.State;
import io.github.jgame.gui.ButtonManager;
import io.github.jgame.gui.ProgressBar;
import io.github.jgame.math.Vector2;
import io.github.jgame.sprite.TextSprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @deprecated only for blitting
 */
@Deprecated
public class ButtonTestState extends State {
    private TextSprite testBt;
    private ProgressBar testBar;

    ButtonTestState(Game p) {
        super(p);
        testBt = new TextSprite("ClickMe", new Font("Arial", Font.PLAIN, 50), Color.BLACK) {
            @Override
            public void onClick(Vector2 mousePos) {
                System.out.println("Button clicked!");
            }
        };
        testBt.pos = new Vector2(200, 200);
        testBt.updateRect();
        ButtonManager.register(testBt);

        testBar = new ProgressBar(10, 20, 10, new Dimension(250, 25), 5,
                Color.RED, Color.BLACK, Color.GRAY);
        testBar.pos = new Vector2(250, 100);
        testBar.updateRect();
    }

    @Override
    public void updateLogic() {
        ButtonManager.update(mouseHandler.mouseSprite);
    }

    @Override
    public void updateGraphics(Graphics g) {
        //testBar.value++;
        //if (testBar.value > testBar.maxVal) {
        //    testBar.value = testBar.minVal;
        //}
        testBar.updateBar();
        Graphics2D g2d = (Graphics2D) g;
        testBt.blitTo(g2d);
        testBt.blitRect(g2d);
        testBar.blit(g2d);
        testBar.blitRect(g2d);
    }

    @Override
    public ButtonMouseHandler getMouseHandler() {
        return new ButtonMouseHandler();
    }

    @Override
    public KeyHandler getKeyHandler() {
        return new ButtonKeyHandler();
    }

    private class ButtonKeyHandler extends KeyHandler {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN: {
                    testBar.value -= 0.5;
                    break;
                }
                case KeyEvent.VK_UP: {
                    testBar.value += 0.5;
                    break;
                }
            }
        }
    }

    private class ButtonMouseHandler extends MouseHandler {
        @Override
        public void mouseMoved(MouseEvent e) {
            mouseSprite.pos = new Vector2(e.getX(), e.getY());
            mouseSprite.updateRect();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            ButtonManager.mouseClicked(mouseSprite);
        }
    }
}
