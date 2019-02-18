package io.github.jgame.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Game extends JPanel {

    public GameRunner runner;
    protected HashMap<String, State> states = new HashMap<>();
    protected String state = "main";

    public Game() {
        states.put("main", new State(this));
    }

    public void setParent(GameRunner parent) {
        runner = parent;
    }

    public void run() {
        setNoEraseBackground();
        addMouseListener(new GameMouseHandler());
        addKeyListener(new GameKeyHandler());
        addMouseMotionListener(new GameMouseMotionHandler());
        addMouseWheelListener(new GameMouseWheelHandler());
        setFocusable(true);
        requestFocusInWindow();

        Timer update = new Timer(0, e -> {
            synchronized (this) {
                updateLogic();
                repaint();
            }
        });
        update.start();

        states.get(state).enter("INIT");
    }

    public void setNoEraseBackground() {
        System.setProperty("sun.awt.noerasebackground", "true");
    }

    public void updateState(String newState) {
        states.get(state).exit(newState);
        String oldState = state;
        state = newState;
        states.get(state).enter(oldState);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        states.get(state).updateGraphics(g);
    }

    private void updateLogic() {
        states.get(state).updateLogic();
    }

    private class GameKeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            states.get(state).keyHandler.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            states.get(state).keyHandler.keyReleased(e);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            states.get(state).keyHandler.keyTyped(e);
        }
    }

    private class GameMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            states.get(state).mouseHandler.mouseMoved(e);
        }
    }

    private class GameMouseWheelHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            states.get(state).mouseHandler.mouseWheelMoved(e);
        }
    }

    private class GameMouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            states.get(state).mouseHandler.mouseClicked(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            states.get(state).mouseHandler.mouseDragged(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            states.get(state).mouseHandler.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            states.get(state).mouseHandler.mouseExited(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            states.get(state).mouseHandler.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            states.get(state).mouseHandler.mouseReleased(e);
        }
    }
}
