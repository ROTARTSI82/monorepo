package com.rotartsi.jgame.gameLogic;

public class IndependentAction {
    public long duration;
    public long cooldown;
    public long actionStarted = 0;
    public long lastUse = 0;
    public long cooldownProgress = 0;
    public long actionProgress = 0;

    public IndependentAction(long duration, long cooldown) {
        this.duration = duration;
        this.cooldown = cooldown;
    }

    public long getCooldownLeft() {
        cooldownProgress = System.currentTimeMillis() - lastUse;
        return cooldown - cooldownProgress;
    }

    public long getActionLeft() {
        actionProgress = System.currentTimeMillis() - actionStarted;
        return duration - actionProgress;
    }

    public void stop() {
        onInterrupt();
        lastUse = System.currentTimeMillis();
        cooldownProgress = cooldown;
        actionProgress = 0;
    }

    public boolean start() {
        cooldownProgress = System.currentTimeMillis() - lastUse;
        if (cooldownProgress >= cooldown) {
            onStart();
            return true;
        } else {
            onReject("cooldown");
        }
        return false;
    }

    public void tick() {
        long now = System.currentTimeMillis();
        actionProgress = now - actionStarted;
        if (actionProgress >= duration) {
            onFinish();
            lastUse = now;
            cooldownProgress = cooldown;
            actionProgress = 0;
        }
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
