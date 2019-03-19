package io.github.jgame.util;


public abstract class GameAction {
    public int priority;  // Can be assigned outside of constructor,
    // and used to determine if this action should interrupt the ongoing action.
    long latestUse;
    long duration;
    long cooldown;
    long progress;
    int id;

    ActionManager parent;

    /**
     * Action that takes {@code duration} to complete has a {@code cooldown} timeout after use
     *
     * @param type     integer containing the action type. (identifier)
     * @param duration duration in miliseconds
     * @param cooldown timeout after use in miliseconds
     */
    public GameAction(int type, long duration, long cooldown) {
        this.latestUse = 0;
        this.progress = 0;

        this.cooldown = cooldown;
        this.duration = duration;
        this.id = type;
        priority = -1;
    }

    public long getCooldown() {
        progress = System.currentTimeMillis() - latestUse;
        if (cooldown > progress) {
            return cooldown - progress;
        }
        return 0;
    }

    public void tick() {

    }

    public void onStart() {

    }

    public void onFinish() {

    }

    public void onReject(String reason) {

    }

    public void onInterrupt() {

    }
}
