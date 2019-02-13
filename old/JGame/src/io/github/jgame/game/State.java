package io.github.jgame.game;

import io.github.jgame.Constants;
import io.github.jgame.math.Vector2;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;

public class State {
    public KeyHandler keyHandler;
    public MouseHandler mouseHandler;
    public MenuHandler menuHandler;

    protected Game game;

    public State(Game parent) {
        game = parent;
        keyHandler = getKeyHandler();
        mouseHandler = getMouseHandler();
        menuHandler = getMenuHandler();
    }

    public KeyHandler getKeyHandler() {
        return new KeyHandler();
    }

    public MouseHandler getMouseHandler() {
        return new MouseHandler();
    }

    public MenuHandler getMenuHandler() {
        return new MenuHandler();
    }

    public double[] getScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return new double[]{size.getWidth(), size.getHeight()};
    }

    public void updateLogic() {

    }

    public void enter(String old_state) {
        game.runner.setJMenuBar(menuHandler);
    }

    public void exit(String new_state) {

    }

    public void updateGraphics(Graphics g) {
        ((Graphics2D) g).setRenderingHints(Constants.RENDER_HINTS);
    }

    public class MenuHandler extends JMenuBar {
        public MenuHandler() {
            super();
            JMenu test = new JMenu("Hey");
            test.addMenuListener(new MenuListener() {
                @Override
                public void menuSelected(MenuEvent e) {
                    System.out.println("select");
                }

                @Override
                public void menuDeselected(MenuEvent e) {
                    System.out.println("deselect");
                }

                @Override
                public void menuCanceled(MenuEvent e) {
                    System.out.println("cancel");
                }
            });

            JMenu submenu = new JMenu("item");

            JMenuItem item = new JMenuItem("sub1");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("click!");
                }
            });
            setHotkey(item, 'N', false);
            submenu.add(item);

            test.addSeparator();
            test.add(submenu);
            //add(test);
        }

        private void setHotkey(JMenuItem item, Character key, boolean shift) {
            item.setAccelerator(KeyStroke.getKeyStroke(key,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() |
                            (shift ? InputEvent.SHIFT_DOWN_MASK : 0)));
        }

        private void setHotkey(JMenuItem item, Character key, int modifiers) {
            item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
        }
    }

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
