package io.github.jgame.game;

import io.github.jgame.Constants;
import io.github.jgame.math.Vector2;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;

import static io.github.jgame.Constants.JGameStr;

/**
 * Support for different "states" of a game.
 * <p>
 * The game will execute different main loop code and
 * handle events differently in different states.
 */
public class State {
    public KeyHandler keyHandler;
    public MouseHandler mouseHandler;

    /**
     * Menu. Extension of {@link JMenuBar}
     */
    public MenuHandler menuHandler;

    protected Game game;

    /**
     * New state!
     *
     * @param parent The Game object.
     */
    public State(Game parent) {
        game = parent;
        keyHandler = getKeyHandler();
        mouseHandler = getMouseHandler();
        menuHandler = getMenuHandler();
    }

    /**
     * Draw the game to the screen. Call's the {@link #game}.repaint() function which in turn
     * calls the {@link #updateGraphics(Graphics)} function.
     * <p>
     * This is rather resource intensive and should be ticked
     * less than {@link #updateLogic()}.
     */
    public void handleRepaint() {
        game.repaint();
    }

    /**
     * Get the handler instance that should be invoked for key events.
     *
     * @return Key handler.
     */
    public KeyHandler getKeyHandler() {
        return new KeyHandler();
    }

    /**
     * Get the handler instance that should be invoked for mouse events.
     *
     * @return Mouse handler.
     */
    public MouseHandler getMouseHandler() {
        return new MouseHandler();
    }

    /**
     * Get the {@link MenuHandler} instance to use. (which is a glorified {@link JMenuBar})
     *
     * @return Menu bar.
     */
    public MenuHandler getMenuHandler() {
        return new MenuHandler();
    }

    public double[] getScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return new double[]{size.getWidth(), size.getHeight()};
    }

    /**
     * Update game logic. No graphical updates.
     */
    public void updateLogic() {

    }

    /**
     * Called whenever the Game's state to this state.
     *
     * @param old_state The state that the game was updated from.
     */
    public void enter(String old_state) {
        game.runner.setJMenuBar(menuHandler);
    }

    /**
     * Called whenever the Game's state is updated from this state.
     *
     * @param new_state The state that the game was updated to.
     */
    public void exit(String new_state) {

    }

    /**
     * Draw things to the screen! Graphical updates here!
     *
     * @param g Graphics to draw to.
     */
    public void updateGraphics(Graphics g) {
        ((Graphics2D) g).setRenderingHints(Constants.RENDER_HINTS);
    }

    /**
     * Menu to be displayed.
     * Extension of {@link javax.swing.JMenuBar}
     */
    public class MenuHandler extends JMenuBar {
        public MenuHandler() {
            super();
            JMenu test = new JMenu(JGameStr.getString("game.State.exampleMenu"));
            test.addMenuListener(new MenuListener() {
                @Override
                public void menuSelected(MenuEvent e) {

                }

                @Override
                public void menuDeselected(MenuEvent e) {

                }

                @Override
                public void menuCanceled(MenuEvent e) {

                }
            });

            JMenu submenu = new JMenu(JGameStr.getString("game.State.exampleSubmenu"));

            JMenuItem item = new JMenuItem(JGameStr.getString("game.State.exampleMenuItem"));
            setHotkey(item, 'N', 0);
            item.addActionListener(e -> {
            });

            submenu.add(item);

            test.addSeparator();
            test.add(submenu);
        }

        /**
         * Set the hotkey for a {@link JMenuItem}
         * <p>
         * NOTE: This uses {@code Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()} which is deprecated.
         * See {@link #setHotkey(JMenuItem, Character, int)}
         *
         * @param item  JMenuItem to update
         * @param key   The hotkey in the form of a character
         * @param shift Require a the shift key to be held
         *
         * @deprecated getMenuShortcutKeyMask() is deprecated. Specify modifiers using {@link #setHotkey(JMenuItem, Character, int)}
         */
        @Deprecated
        protected void setHotkey(JMenuItem item, Character key, boolean shift) {
            item.setAccelerator(KeyStroke.getKeyStroke(key,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() |
                            (shift ? InputEvent.SHIFT_DOWN_MASK : 0)));
        }

        /**
         * Set the hotkey for a {@link JMenuItem}
         *
         * @param item JMenuItem to update
         * @param key Key as a character
         * @param modifiers Mask of key modifiers that are required as an integer
         */
        protected void setHotkey(JMenuItem item, Character key, int modifiers) {
            item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
        }
    }

    /**
     * Handles all key events.
     */
    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        private boolean hasMod(KeyEvent e, int mask) {
            return (e.getModifiersEx() & mask) != 0;
        }
    }

    /**
     * Handles all mouse events
     */
    public class MouseHandler extends MouseAdapter {
        public Vector2 pos = new Vector2(0, 0);

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }
    }
}
