package io.github.jgame.event;

import java.awt.*;
import java.awt.event.KeyEvent;

public class EventRobot extends Robot {
    public EventRobot() throws AWTException {
        super();
        this.setAutoDelay(1);
    }

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

    public void clickMouse(int holdTime, int button) {
        if (button < 1 || button > 3) {
            throw new IllegalArgumentException("Button needs to be between 1 and 3, not " + button);
        }
        this.mousePress(1 << (9 + button));
        this.delay(holdTime);
        this.mouseRelease(1 << (9 + button));
    }
}
