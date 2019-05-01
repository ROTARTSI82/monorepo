package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.game.GameRunner;

import java.awt.*;

/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */
/**
 * @deprecated Only use for blitting tests.
 */
@Deprecated
public class Main extends GameRunner {
    Main(TestGame inst) {
        super(inst);
    }

    public static void main(String[] args) {
        //Log4jLogger.setup("mainDemo");
        //GenericLogger.setup(Level.ALL, Level.ALL, Level.OFF, "logs/mainDemo");
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
