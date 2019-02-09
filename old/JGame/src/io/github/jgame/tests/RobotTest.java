package io.github.jgame.tests;

import io.github.jgame.event.EventRobot;

import java.awt.*;
import java.util.Random;

public class RobotTest {
    private static final Random rand = new Random();
    private static final int range = 128;

    public static void main(String[] args) {
        EventRobot robot;
        try {
            robot = new EventRobot();
            while (true) {
                robot.type(getRandString(), rand.nextInt(range), rand.nextInt(range));
                robot.delay(rand.nextInt(range));
                robot.mouseMove(rand.nextInt(1440), rand.nextInt(900));
                robot.delay(rand.nextInt(range));
                robot.clickMouse(rand.nextInt(range), rand.nextInt(3) + 1);
                robot.delay(rand.nextInt(range));
                robot.mouseWheel(rand.nextInt(range / 2) - range / 4);
                robot.delay(rand.nextInt(range));
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
