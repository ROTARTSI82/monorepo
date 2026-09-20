package com.rotartsi.jgame.mechanics;

public interface Meters {
    void tick();

    boolean isOperational();

    class HeatMeter implements Meters {
        public int heat;
        public boolean overheated = false;
        public boolean isGaining = false;
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

    class FuelMeter implements Meters {
        public boolean isUsingFuel = false;
        public double fuel = 0;
        public double loss = 0;

        public long lastUpdate = System.currentTimeMillis();

        public FuelMeter(double amount, double lossRate) {
            fuel = amount;
            loss = lossRate;
        }

        public boolean isOperational() {
            return fuel > 0;
        }

        public void tick() {
            long now = System.currentTimeMillis();
            double frameMult = (now - lastUpdate) / 1000d;

            fuel -= loss * frameMult;

            lastUpdate = now;
        }

        public void addFuel(double amount) {
            fuel += amount;
        }
    }

    class AmmoMeter implements Meters {
        public int reserve;

        public AmmoMeter(int amount) {
            reserve = amount;
        }

        public void tick() {

        }

        public boolean isOperational() {
            return reserve > 0;
        }
    }

}
