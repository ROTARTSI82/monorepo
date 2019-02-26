package io.github.jgame.tests.window;

import io.github.jgame.game.Game;

/**
 * @deprecated Only use for blitting tests.
 */
@Deprecated
public class TestGame extends Game {
    public TestGame() {
        super();
        states.put("main", new MainState(this));
        states.put("win_capture", new WindowCaptureState(this));
        states.put("btTest", new ButtonTestState(this));
        state = "btTest";
    }
}
