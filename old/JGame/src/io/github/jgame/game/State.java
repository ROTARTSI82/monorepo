package io.github.jgame.game;

import io.github.jgame.math.Vector2;

import java.awt.*;
import java.awt.event.*;

public class State {
    public KeyHandler keyHandler;
    public MouseHandler mouseHandler;

    protected Game game;

    public State(Game parent) {
        game = parent;
        keyHandler = getKeyHandler();
        mouseHandler = getMouseHandler();
    }

    public KeyHandler getKeyHandler() {
        return new KeyHandler();
    }

    public MouseHandler getMouseHandler() {
        return new MouseHandler();
    }

    public double[] getScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return new double[]{size.getWidth(), size.getHeight()};
    }

    public void updateLogic() {

    }

    public void enter(String old_state) {

    }

    public void exit(String new_state) {

    }

    public void updateGraphics(Graphics g) {

    }

    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
            //System.out.println(e.paramString());
        }
    }

    public class MouseHandler extends MouseAdapter {
        public Vector2 pos = new Vector2(0, 0);

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //System.out.println(e.paramString());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //System.out.println(e.paramString());
        }
    }
}
