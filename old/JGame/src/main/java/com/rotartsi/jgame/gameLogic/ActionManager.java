package com.rotartsi.jgame.gameLogic;

/**
 * ActionManager. Used to run and manage {@link GameAction}s.
 */
public class ActionManager {
    /**
     * The current action that is active.
     */
    public GameAction currentAction;
    /**
     *
     */
    public long actionProgress;
    /**
     * The millisecond time stamp that the action started.
     */
    public long startTime;

    /**
     * Create a new manager!
     */
    public ActionManager() {
        reset();
    }

    /**
     * Reset all fields. Close the {@link #currentAction} properly by setting cooldownProgress and latestUse
     */
    private void reset() {
        if (currentAction != null) {
            currentAction.latestUse = System.currentTimeMillis();
            currentAction.cooldownProgress = currentAction.cooldown;
            currentAction.parent = null;
        }

        currentAction = null;

        actionProgress = 0;
        startTime = 0;
    }

    /**
     * Tick the action manager. See if the action has finished and call {@link GameAction}.tick()
     */
    public void tick() {
        if (currentAction != null) {
            long now = System.currentTimeMillis();
            currentAction.tick();
            if (currentAction == null) {  // Return if the tick routine canceled the action.
                return;
            }
            actionProgress = now - startTime;
            if (actionProgress > currentAction.duration) {  // Action is done!
                currentAction.onFinish();
                reset();
            }
        }
    }

    /**
     * Stop the current action.
     */
    public void stop() {
        if (currentAction != null) {
            currentAction.onInterrupt();
            reset();
        }
    }

    /**
     * Try to do a new action.
     * Checks for cooldowns and duplicates.
     *
     * example usage:
     * <code>manager.doAction(jumpAction, jumpAction.priority &gt; manager.currentAction.priority, true);</code>
     *
     * @param action           Action to do
     * @param interrupt        If the action should interrupt the current one.
     * @param rejectDuplicates If the action should be rejected if ids match.
     * @return true if successful
     */
    public boolean doAction(GameAction action, boolean interrupt, boolean rejectDuplicates) {
        long now = System.currentTimeMillis();
        if (now - action.latestUse < action.cooldown) {  // The action is still on cooldown!
            action.onReject("cooldown");
            return false;
        }
        if (currentAction == null) {  // No active action. Do the action.
            action.parent = this;
            currentAction = action;
            startTime = now;
            action.onStart();
            return true;
        }

        if (action.id == currentAction.id && rejectDuplicates) {  // Duplicate of current action. Reject.
            action.onReject("duplicate");
            return false;
        }
        if (interrupt) {  // This action takes priority over the current action
            action.parent = this;
            currentAction.onInterrupt();
            reset();
            currentAction = action;
            startTime = now;
            currentAction.onStart();
            return true;
        }
        action.onReject("busy");  // Current action takes priority over this action!
        return false;
    }
}
