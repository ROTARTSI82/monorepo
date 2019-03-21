package io.github.jgame.event;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Get the state of certain keys without the use of a {@code JPanel}
 *
 * JPanel's key managers are preferred.
 */
public class IndependentKeyTracker implements KeyEventDispatcher {
    /**
     * The state of the keys. Queried using {@link #isPressed(int)} and updated using {@link #dispatchKeyEvent(KeyEvent)}
     */
    private volatile HashMap<Integer, Boolean> keyStates = new HashMap<>();

    /**
     * Create new key tracker. Adds this to KeyboardFocusManager
     */
    public IndependentKeyTracker() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    /**
     * Event handler for when a key is pressed.
     *
     * @param e Event
     * @return always false.
     */
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

    /**
     * See if a certail key is pressed.
     *
     * @param keycode Keycode (e.g. {@code KeyEvent.VK_DOWN;  // 0x28}
     * @return True if key is pressed. False if uncertain.
     */
    public boolean isPressed(int keycode) {
        synchronized (IndependentKeyTracker.class) {
            if (keyStates.containsKey(keycode)) {
                return keyStates.get(keycode);
            }
            return false;
        }
    }
}
