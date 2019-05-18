package com.rotartsi.jgame.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Template for the main program.
 * <p>
 * Extension of {@link javax.swing.JFrame}
 */
public class GameRunner extends JFrame {

    /**
     * The game that is running (See {@link Game})
     */
    public Game game;

    /**
     * Run the game instance!
     *
     * @param gameInst Game instance to run. See {@link Game}
     */
    public GameRunner(Game gameInst) {
        game = gameInst;
        game.setParent(this);
        add(game);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setResizable(true);
        setSize(640, 480);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                game.states.get(game.state).exit("SHUTDOWN");
                System.exit(0);
            }
        });

    }

    /**
     * Add our game's main loop to the AWT event loop.
     * Just call {@link #run}
     *
     * @param args Ignored. Doesn't matter.
     */
    public static void main(String[] args) {
        run(new Game());
    }

    /**
     * Add the main loop to the AWT event loop.
     *
     * @param game Game to run
     */
    protected static void run(Game game) {
        EventQueue.invokeLater(() -> {
            GameRunner app = new GameRunner(game);
            app.game.run();
            app.setVisible(true);
        });
    }
}
