package io.github.jgame.util;

public class ActionManager {
    public GameAction currentAction;

    private long progress;
    private long startTime;
    private long duration;

    public ActionManager() {
        reset();
    }

    private void reset() {
        if (currentAction != null) {
            currentAction.latestUse = System.currentTimeMillis();
            currentAction.progress = currentAction.cooldown;
        }

        currentAction = null;

        progress = 0;
        startTime = 0;
        duration = 0;
    }

    public void tick() {
        if (currentAction != null) {
            long now = System.currentTimeMillis();
            currentAction.tick();
            if (currentAction == null) {  // Return if the tick routine canceled the action.
                return;
            }
            progress = now - startTime;
            if (progress > currentAction.duration) {  // Action is done!
                currentAction.onFinish();
                reset();
            }
        }
    }

    public void stop() {
        if (currentAction != null) {
            currentAction.onInterrupt();
            reset();
        }
    }

    public void doAction(GameAction action, boolean interrupt, boolean rejectDuplicates) {
        long now = System.currentTimeMillis();
        if (now - action.latestUse < action.cooldown) {  // The action is still on cooldown!
            action.onReject("cooldown");
            return;
        }
        if (currentAction == null) {  // No active action. Do the action.
            action.parent = this;
            currentAction = action;
            startTime = now;
            action.onStart();
            duration = action.duration;
            return;
        }

        if (action.id == currentAction.id && rejectDuplicates) {  // Duplicate of current action. Reject.
            action.onReject("duplicate");
            return;
        }
        if (interrupt) {  // This action takes priority over the current action
            currentAction.onInterrupt();
            reset();
            currentAction = action;
            startTime = now;
            currentAction.onStart();
            duration = currentAction.duration;
            return;
        }
        action.onReject("busy");  // Current action takes priority over this action!
    }
}
