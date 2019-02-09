package io.github.jgame.event;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class IndependentKeyTracker implements KeyEventDispatcher {
    private volatile HashMap<Integer, Boolean> keyStates = new HashMap<>();

    public IndependentKeyTracker() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        synchronized (IndependentKeyTracker.class) {
            int eventType = e.getID();
            if (eventType == KeyEvent.KEY_PRESSED) {
                keyStates.put(e.getKeyCode(), true);
            }
            if (eventType == KeyEvent.KEY_RELEASED) {
                keyStates.put(e.getKeyCode(), false);
            }
        }
        return false;
    }

    public boolean isPressed(int keycode) {
        synchronized (IndependentKeyTracker.class) {
            if (keyStates.containsKey(keycode)) {
                return keyStates.get(keycode);
            }
            return false;
        }
    }
}
