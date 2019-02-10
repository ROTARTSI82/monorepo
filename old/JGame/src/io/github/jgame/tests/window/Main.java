package io.github.jgame.tests.window;

import io.github.jgame.game.GameRunner;

import java.awt.*;

public class Main extends GameRunner {
    Main(TestGame inst) {
        super(inst);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main app = new Main(new TestGame());
                app.game.run();
                app.setVisible(true);
            }
        });
    }
}
