package com.rotartsi.jgame.gameLogic;

/**
 * Support for actions, something that take some time to complete and have a cooldown
 * before they can be used again.
 */
public abstract class GameAction {
    /**
     * Can be assigned outside of constructor,
     * and used to determine if this action should interrupt the ongoing action.
     * <p>
     * Example usage would be:
     * {@code actionManager.doAction(action, action.priority &gt; actionManager.currentAction.priority, true);}
     * <p>
     * See {@link ActionManager}
     */
    public int priority;

    /**
     * The last time the action was used. Used to calculate the cooldown.
     */
    long latestUse;

    /**
     * The amount of time the action takes to complete.
     */
    long duration;

    /**
     * The amount of time you have to wait before this action can be done again
     */
    long cooldown;

    /**
     * The amount of time that has elapsed since the action was last done. Used for cooldown
     */
    long cooldownProgress;

    /**
     * The action id.
     */
    int id;

    /**
     * The weapon. Is null when the action is not being done.
     */
    ActionManager parent;

    /**
     * Action that takes {@code duration} to complete has a {@code cooldown} timeout after use
     * Action Types:
     * - 0: RELOAD
     *
     * @param type     integer containing the action type. (identifier)
     * @param duration duration in miliseconds
     * @param cooldown timeout after use in miliseconds
     */
    public GameAction(int type, long duration, long cooldown) {
        this.latestUse = 0;
        this.cooldownProgress = 0;

        this.cooldown = cooldown;
        this.duration = duration;
        this.id = type;
        priority = -1;
    }

    /**
     * Get the amount of time left before the action can be used again.
     *
     * @return Time in milliseconds (can be negative if the cooldown is already over)
     */
    public long getCooldownLeft() {
        cooldownProgress = System.currentTimeMillis() - latestUse;
        return cooldown - cooldownProgress;
    }

    public long getActionLeft() {
        if (parent != null) {
            parent.actionProgress = -System.currentTimeMillis() - parent.startTime;
            return duration - parent.actionProgress;
        } else {
            return 0;
        }
    }

    /**
     * Called every ActionManager.tick(). See {@link ActionManager}
     */
    public void tick() {

    }

    /**
     * Called when the action is started.
     */
    public void onStart() {

    }

    /**
     * Called when the action is completed properly.
     */
    public void onFinish() {

    }

    /**
     * Called when the action is rejected.
     *
     * @param reason The reason the action was rejected.
     */
    public void onReject(String reason) {

    }

    /**
     * Called when the action is interrupted.
     */
    public void onInterrupt() {

    }
}
