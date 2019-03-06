package io.github.jgame.tests.window;

import io.github.jgame.game.GameRunner;
import io.github.jgame.logging.GenericLogger;

import java.awt.*;
import java.util.logging.Level;

/**
 * @deprecated Only use for blitting tests.
 */
@Deprecated
public class Main extends GameRunner {
    Main(TestGame inst) {
        super(inst);
    }

    public static void main(String[] args) {
        GenericLogger.setup(Level.ALL, Level.ALL, Level.OFF, "logs/mainDemo");
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
