package com.rotartsi.jgame.tests.window;

import com.rotartsi.jgame.game.Game;

/*
THIS CODE SHOULD NOT BE IN PRODUCTION. IF IT IS, SOMEBODY SCREWED UP!
 */

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
        state = "main";
    }
}
