package io.github.jgame.tests;

import io.github.jgame.game.Game;

public class TestGame extends Game {
    public TestGame() {
        super();
        states.put("main", new MainState(this));
        states.put("win_capture", new WindowCaptureState(this));
        state = "main";
    }
}
