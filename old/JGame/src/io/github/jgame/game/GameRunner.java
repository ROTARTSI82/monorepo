package io.github.jgame.game;

import javax.swing.*;
import java.awt.*;

public class GameRunner extends JFrame {
    public Game game;

    public GameRunner() {
    }

    public GameRunner(Game gameInst) {
        game = gameInst;
        game.setParent(this);
        add(game);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setResizable(true);
        setIconImage(new ImageIcon("dat/sprite.png").getImage());
        setSize(640, 480);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GameRunner app = new GameRunner(new Game());
                app.game.run();
                app.setVisible(true);
            }
        });
    }
}
