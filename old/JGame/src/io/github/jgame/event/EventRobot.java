package io.github.jgame.event;

import java.awt.*;

public class EventRobot extends Robot {
    public EventRobot() throws AWTException {
        super();
    }

    public void type(String msg, int waitBetween, int holdTime) {
        for (byte b : msg.getBytes()) {
            int code = b;
            if (code > 96 && code < 123) code -= 32; // Make sure that the keycode is in range.
            this.delay(waitBetween);
            this.keyPress(code);
            this.delay(holdTime);
            this.keyRelease(code);
        }
    }

    public void clickMouse(int holdTime, int button) {
        this.mousePress(1 << (9 + button));
        this.delay(holdTime);
        this.mouseRelease(1 << (9 + button));
    }
}
