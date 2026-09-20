package com.rotartsi.jgame.game;

import com.rotartsi.jgame.event.AnyEvent;

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
    /**
     * The "parent" runner that is running this game. See {@link GameRunner}
     */
    public GameRunner runner;

    /**
     * A HashMap containing all of the states for this game.
     */
    protected HashMap<String, State> states = new HashMap<>();

    /**
     * The current state of the game. This dictates what event handlers are active and what
     * code is being executed for the mainloop (both blitting and logic updates)
     */
    protected String state = "main";

    /**
     * Should be called by the GameRunner (JFrame) to set the {@link #runner}.
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
     * Repaint the surface. Calls the current state's handleRepaint()
     *
     * This in turn causes {@link #paintComponent(Graphics)} to be called.
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

        /**
         * Handler for KEYDOWN events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Key event
         */
        @Override
        public void keyPressed(KeyEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.KeyHandler h : current.keyHandlers) {
                h.keyPressed(e);
            }
        }

        /**
         * Handler for KEYUP events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Key event
         */
        @Override
        public void keyReleased(KeyEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.KeyHandler h : current.keyHandlers) {
                h.keyReleased(e);
            }
        }

        /**
         * Handler for generic key events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Key event
         */
        @Override
        public void keyTyped(KeyEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.KeyHandler h : current.keyHandlers) {
                h.keyTyped(e);
            }
        }
    }

    /**
     * Forward all mouse motion events to the current state's event handler.
     */
    private class GameMouseMotionHandler extends MouseMotionAdapter {

        /**
         * Handler for MOUSEMOVE events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseMoved(e);
            }
        }
    }

    /**
     * Forward all mouse wheel events to the current state's event handler.
     */
    private class GameMouseWheelHandler implements MouseWheelListener {

        /**
         * Handler for scrolling events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseWheelMoved(e);
            }
        }
    }

    /**
     * Forward all mouse events (excluding mouse wheel and mouse motion events. See {@link GameMouseHandler}
     * and {@link GameMouseWheelHandler}) to the current state's event handler.
     */
    private class GameMouseHandler extends MouseAdapter {

        /**
         * Handler for MOUSEBUTTONDOWN events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseClicked(e);
            }
        }

        /**
         * Handler for mouse drag events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseDragged(e);
            }
        }

        /**
         * Handler for mouse enter events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseEntered(e);
            }
        }

        /**
         * Handler for mouse exit events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseExited(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseExited(e);
            }
        }

        /**
         * Handler for MOUSEBUTTONDOWN events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mousePressed(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mousePressed(e);
            }
        }

        /**
         * Handler for MOUSEBUTTONUP events.
         * Forwards all events to the current state's event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            State current = states.get(state);
            current.onEvent(new AnyEvent(e));
            for (State.MouseHandler h : current.mouseHandlers) {
                h.mouseReleased(e);
            }
        }
    }
}
