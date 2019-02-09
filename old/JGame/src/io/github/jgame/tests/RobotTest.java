package io.github.jgame.tests;

import io.github.jgame.event.EventRobot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class RobotTest {
    private static final Random rand = new Random();
    private static final int range = 128;

    public static void main(String[] args) {
        EventRobot robot;
        try {
            robot = new EventRobot();
            while (true) {
                /*
                DO NOT RUN NO MATTER WHAT,
                 NOT EVEN IF THE SUN IS ABOUT TO ENVELOP THE EARTH!
                  I SPEAK FROM PERSONAL EXPERIENCE!!!!
                  **-**-**-**-**-**-**-**-**-**-**-**-**-**-**-**-**
                robot.typeAscii(getRandString(), 0, 0);
                robot.mouseMove(rand.nextInt(1440), rand.nextInt(900));
                robot.clickMouse(0, rand.nextInt(3) + 1);
                robot.mouseWheel(rand.nextInt(range) - range / 2);
                */
                robot.delay(75);
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_W);
                robot.keyPress(KeyEvent.VK_D);

                robot.delay(500);

                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_W);
                robot.keyRelease(KeyEvent.VK_D);
                robot.delay(1);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static String getRandString() {
        int len = rand.nextInt(128);
        byte[] bytes = new byte[len];
        rand.nextBytes(bytes);
        return new String(bytes);
    }
}
