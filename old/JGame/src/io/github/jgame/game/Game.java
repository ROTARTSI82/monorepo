package io.github.jgame.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * Extension of {@link javax.swing.JPanel}
 * <p>
 * Manages multiple states and inputs from keyboard and mouse.
 */
public class Game extends JPanel {

    public GameRunner runner;

    protected HashMap<String, State> states = new HashMap<>();
    protected String state = "main";

    /**
     * Should be called by the GameRunner (JPanel) to set the {@link #runner}.
     *
     * @param parent GameRunner parent
     */
    public void setParent(GameRunner parent) {
        runner = parent;
    }

    /**
     * Launch the game and start the main loop.
     */
    public void run() {
        setNoEraseBackground();
        addMouseListener(new GameMouseHandler());
        addKeyListener(new GameKeyHandler());
        addMouseMotionListener(new GameMouseMotionHandler());
        addMouseWheelListener(new GameMouseWheelHandler());
        setFocusable(true);
        requestFocusInWindow();

        for (Timer timer : getTimers()) {
            timer.start();
        }

        states.get(state).enter("INIT");
    }

    /**
     * Defaults to a single timer called as fast as possible, calling updateLogic() and handleRepaint()
     *
     * @return List of timers this game uses.
     */
    public Timer[] getTimers() {
        return new Timer[]{new Timer(0, e -> {
            synchronized (this) {
                updateLogic();
                handleRepaint();
            }
        })};
    }

    /**
     * Repaint the surface. Defaults to calling the current state's handleRepaint()
     */
    public void handleRepaint() {
        states.get(state).handleRepaint();
    }

    /**
     * Seems to have no effect. Doesn't work.
     */
    public void setNoEraseBackground() {
        System.setProperty("sun.awt.noerasebackground", "true");
    }

    /**
     * Handle switching the state of the game.
     * Call the oldState's exit() function and the newState's enter() function
     *
     * @param newState State to update to.
     */
    public void updateState(String newState) {
        states.get(state).exit(newState);
        String oldState = state;
        state = newState;
        states.get(state).enter(oldState);
    }

    /**
     * Call current state's {@code updateGraphics()}
     *
     * @param g Graphics to draw to.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        states.get(state).updateGraphics(g);
    }

    /**
     * Call current state's {@code updateLogic()}
     */
    public void updateLogic() {
        states.get(state).updateLogic();
    }

    /**
     * Forward all Keyboard events to the current state's event handler.
     */
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

    /**
     * Forward all mouse motion events to the current state's event handler.
     */
    private class GameMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            states.get(state).mouseHandler.mouseMoved(e);
        }
    }

    /**
     * Forward all mouse wheel events to the current state's event handler.
     */
    private class GameMouseWheelHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            states.get(state).mouseHandler.mouseWheelMoved(e);
        }
    }

    /**
     * Forward all mouse events (excluding mouse wheel and mouse motion events. See {@link GameMouseHandler}
     * and {@link GameMouseWheelHandler}) to the current state's event handler.
     */
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
