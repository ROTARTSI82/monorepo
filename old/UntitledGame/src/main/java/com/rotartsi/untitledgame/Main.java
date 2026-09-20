package com.rotartsi.untitledgame;

import com.rotartsi.jgame.game.Game;
import com.rotartsi.jgame.game.GameRunner;

public class Main extends GameRunner {

    public Main(Game inst) {
        super(inst);
    }

    public static void main(String[] args) {
        run(new MainGame());
    }
}
