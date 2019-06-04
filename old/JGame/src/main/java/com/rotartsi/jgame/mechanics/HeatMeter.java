package com.rotartsi.jgame.mechanics;

public class HeatMeter {
    public int heat;
    public boolean overheated = false;
    public boolean isGaining;
    public int cooling;
    public int gain;

    public int minHeat;

    public int maxHeat;

    public int maxHeatThreshold;
    public int minHeatThreshold;
    public long lastTick = System.currentTimeMillis();


    public HeatMeter(int min, int max, int minHeat, int maxHeat) {
        this.minHeat = min;
        this.maxHeat = max;
        maxHeatThreshold = maxHeat;
        minHeatThreshold = minHeat;
        cooling = 0;
        heat = 0;
        gain = 0;
    }

    public void tick() {
        long now = System.currentTimeMillis();
        double heatMult = (now - lastTick) / 1000d;
        heat -= cooling * heatMult;
        if (isGaining) {
            heat += gain * heatMult;
        }
        heat = Math.max(minHeat, Math.min(maxHeat, heat));

        if (heat >= maxHeatThreshold) {
            overheated = true;
        } else if (heat <= minHeatThreshold) {
            overheated = false;
        }
        lastTick = now;
    }

    public boolean isOperational() {
        return heat >= minHeatThreshold && heat <= maxHeatThreshold && !overheated;
    }

}
