package com.rotartsi.untitledgame;

import com.rotartsi.jgame.game.Game;
import com.rotartsi.untitledgame.states.TestingState;

import javax.swing.*;

public class MainGame extends Game {
    public MainGame() {
        states.put("main", new TestingState(this));
    }

    @Override
    public Timer[] getTimers() {
        return new Timer[]{new Timer(0, (e) -> { // Do logic updates as fast as possible
            synchronized (this) {
                this.updateLogic();
            }
        }), new Timer(1000 / 15, (e) -> {  // Update graphics at 15 fps
            synchronized (this) {
                this.handleRepaint();
            }
        })};
    }
}
