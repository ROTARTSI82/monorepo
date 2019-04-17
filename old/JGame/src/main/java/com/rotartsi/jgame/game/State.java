package com.rotartsi.jgame.game;

import com.rotartsi.jgame.Constants;
import com.rotartsi.jgame.event.AnyEvent;
import com.rotartsi.jgame.gui.ButtonManager;
import com.rotartsi.jgame.sprite.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Support for different "states" of a game.
 * <p>
 * The game will execute different main loop code and
 * handle events differently in different states.
 */
public class State {

    /**
     * Event handler for all keyboard events
     */
    public KeyHandler[] keyHandlers;

    /**
     * Event handler for all mouse events
     */
    public MouseHandler[] mouseHandlers;

    /**
     * Menu. Extension of {@link JMenuBar}
     */
    public MenuHandler menuHandler;

    /**
     * The current game that this state is running on. Effectively the state's "parent"
     * See {@link Game}
     */
    protected Game game;

    /**
     * New state!
     *
     * @param parent The Game object.
     */
    public State(Game parent) {
        game = parent;
        keyHandlers = getKeyHandlers();
        mouseHandlers = getMouseHandlers();
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
    public KeyHandler[] getKeyHandlers() {
        return new KeyHandler[]{new KeyHandler()};
    }

    /**
     * Get the handler instance that should be invoked for mouse events.
     *
     * @return Mouse handler.
     */
    public MouseHandler[] getMouseHandlers() {
        return new MouseHandler[]{new MouseHandler()};
    }

    /**
     * Get the {@link MenuHandler} instance to use. (which is a glorified {@link JMenuBar})
     *
     * @return Menu bar.
     */
    public MenuHandler getMenuHandler() {
        return new MenuHandler();
    }

    /**
     * Get the available space for blitting. (This is the size of the JFrame)
     *
     * @return Dimensions of the JFrame
     */
    public Dimension getScreenSize() {
        return game.runner.getSize();
    }

    /**
     * Get the size of the physical display as opposed to the window.
     *
     * @return Dimensions of physical display
     */
    public Dimension getDisplaySize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Set the cursor. Passed into {@code Cursor.getPredefinedCursor(cursor)}
     * See {@link Cursor}
     *
     * @param cursor predefined cursor id
     */
    public void setCursor(int cursor) {
        game.setCursor(Cursor.getPredefinedCursor(cursor));
    }

    /**
     * Update game logic. No graphical updates.
     */
    public void updateLogic() {

    }

    public void onEvent(AnyEvent event) {

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

        /**
         * Create a new menu! Everything should be initialized in here. Override this constructor
         * to create a custom menu.
         */
        public MenuHandler() {
            super();
            /*
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
            */
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

        /**
         * Handler for KEYDOWN events.
         * Override this for custom event handler.
         *
         * @param e Key event
         */
        @Override
        public void keyPressed(KeyEvent e) {

        }

        /**
         * Handler for KEYUP events.
         * Override this for custom event handler.
         *
         * @param e Key event
         */
        @Override
        public void keyReleased(KeyEvent e) {

        }

        /**
         * Handler for generic key events.
         * Override this for custom event handler.
         *
         * @param e Key event
         */
        @Override
        public void keyTyped(KeyEvent e) {

        }

        /**
         * Check if a KeyEvent has the specified modifiers.
         *
         * @param e    The key event
         * @param mask The modifier mask to check for
         * @return true if the mask is successful.
         */
        protected boolean hasMod(KeyEvent e, int mask) {
            return (e.getModifiersEx() & mask) != 0;
        }
    }

    /**
     * Handles all mouse events
     */
    public class MouseHandler extends MouseAdapter {
        /**
         * Sprite for tracking mouse. See {@link ButtonManager}.
         */
        public Sprite mouseSprite = new Sprite(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));

        /**
         * Handler for MOUSEMOVE events.
         * Override this for custom event handler.
         *
         * <b>
         * Be sure to call super for this handler! Otherwise the {@link #mouseSprite} won't be updated!
         * </b>
         *
         * @param e Mouse event
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            mouseSprite.pos.x = e.getX();
            mouseSprite.pos.y = e.getY();
            mouseSprite.updateRect();
        }

        /**
         * Handler for mouse wheel events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        /**
         * Handler for MOUSEBUTTONDOWN events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        /**
         * Handler for mouse drag events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseDragged(MouseEvent e) {

        }

        /**
         * Handler for mouse entering events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseEntered(MouseEvent e) {

        }

        /**
         * Handler for mouse exiting events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseExited(MouseEvent e) {

        }

        /**
         * Handler for MOUSEBUTTONDOWN events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mousePressed(MouseEvent e) {

        }

        /**
         * Handler for MOUSEBUTTONUP events.
         * Override this for custom event handler.
         *
         * @param e Mouse event
         */
        @Override
        public void mouseReleased(MouseEvent e) {

        }
    }
}
