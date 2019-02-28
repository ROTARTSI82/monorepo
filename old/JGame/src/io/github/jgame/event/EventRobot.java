package io.github.jgame.event;

import java.awt.*;
import java.awt.event.KeyEvent;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

/**
 * Extension of {@link java.awt.Robot}
 * <p>
 * Adds easy way for Robot to type out String literals and click and release mouse.
 */
public class EventRobot extends Robot {
    public EventRobot() throws AWTException {
        super();
        this.setAutoDelay(1);  // Make sure that all events get registered before moving on.
    }

    /**
     * Simulate the user typing a string literal
     *
     * @param msg         String literal to type
     * @param waitBetween Time to wait in milliseconds in between each keystroke
     * @param holdTime    Time to hold down each key in milliseconds
     */
    public void typeAscii(String msg, int waitBetween, int holdTime) {
        for (char b : msg.toCharArray()) {
            this.delay(waitBetween);

            int code = KeyEvent.getExtendedKeyCodeForChar(b);
            if (code == 0) continue;

            boolean useShift = Character.isUpperCase(b);

            if (useShift) {
                this.keyPress(KeyEvent.VK_SHIFT);
            }
            this.keyPress(code);
            this.delay(holdTime);
            this.keyRelease(code);
            if (useShift) {
                this.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }

    /**
     * Get the color of a specific pixel on the screen in the form of a int array.
     *
     * @param x X position on the screen (pixels)
     * @param y Y position on the screen (pixels)
     * @return Color (and alpha) in the form of an array. {@code new int[]{red, green, blue, alpha}}
     */
    public int[] getPixelRGBA(int x, int y) {
        Color color = getPixelColor(x, y);
        return new int[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
    }

    /**
     * Simulate the user clicking and releasing the mouse
     *
     * @param holdTime Time to hold the mousebutton down
     * @param button Mouse button to press (integer between 1 and 3 inclusive)
     */
    public void clickMouse(int holdTime, int button) {
        if (button < 1 || button > 3) {
            throw new IllegalArgumentException(fmt(JGameStr.getString("event.EventRobot.invalidButton"),
                    button));
        }
        this.mousePress(1 << (9 + button));
        this.delay(holdTime);
        this.mouseRelease(1 << (9 + button));
    }
}
