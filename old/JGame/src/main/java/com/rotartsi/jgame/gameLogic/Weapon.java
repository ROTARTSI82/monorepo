package com.rotartsi.jgame.gameLogic;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.BulletFactory;
import com.rotartsi.jgame.mechanics.Meters;
import com.rotartsi.jgame.mechanics.PlatformerPlayer;

/**
 * NOT YET IMPLEMENTED
 */
public class Weapon {

    public ActionManager manager = new ActionManager();

    public boolean doReloads = true;

    public int ammo;
    public int magSize;
    public double dps;
    public Meters.AmmoMeter meter;
    public boolean reloading = false;
    public double shotDamage;
    public long shotCooldown;
    public long lastFire = System.currentTimeMillis();
    public PlatformerPlayer owner;
    public ReloadAction reloadAction;
    public BulletFactory factory;
    public String name = "Generic Weapon";

    /**
     * Use this constructor for reloads (use other constructor for directly depleting the Ammo supply instead of using
     * magazines)
     *
     * @param dps Damage per second
     * @param rof Rate of Fire (Shots per second)
     * @param bullet Bullet Factory
     * @param owner Owner of the weapon
     * @param magSize Magazine size
     * @param meter Ammo meter
     * @param reloadDuration Reload time (miliseconds)
     */
    public Weapon(double dps, double rof, BulletFactory bullet, PlatformerPlayer owner,
                  int magSize, Meters.AmmoMeter meter, long reloadDuration) {
        shotDamage = dps / rof;
        this.dps = dps;
        factory = bullet;
        shotCooldown = (long) (1000 / rof);
        this.owner = owner;
        this.magSize = magSize;
        ammo = magSize;
        this.meter = meter;
        reloadAction = new ReloadAction(reloadDuration, this);
    }

    public Weapon(double dps, double rof, BulletFactory bullet, PlatformerPlayer owner, Meters.AmmoMeter meter) {
        shotDamage = dps / rof;
        this.dps = dps;
        factory = bullet;
        shotCooldown = (long) (1000 / rof);
        this.owner = owner;
        this.magSize = 0;
        ammo = 0;
        this.meter = meter;
        reloadAction = null;
        doReloads = false;
    }

    public void forceReload() {
        if (ammo < magSize && !reloading && doReloads) {
            manager.doAction(reloadAction, true, true);
        }
        // Other two cases:  Already reloading or no need to reload.
    }

    public void tick() {
        if (ammo <= 0 && !reloading && meter.reserve > 0 && doReloads) {
            manager.doAction(reloadAction, true, true);
        }
        manager.tick();
        factory.tick();
    }

    private void fire(Vector2 targetPos, double damage) {
        if (ammo <= 0 && doReloads) {
            return;
        } else if (!doReloads && meter.reserve <= 0) {
            return;
        }
        onFire();
        factory.addBullet(targetPos, damage, owner);
        factory.onFire();
        if (doReloads) {
            ammo -= 1;
        } else {
            meter.reserve -= 1;
        }
    }

    public void tryFire(Vector2 target, boolean recallDamage) {
        long now = System.currentTimeMillis();
        if (now - lastFire > shotCooldown) {
            if (!recallDamage) {
                fire(target, shotDamage);
            } else {
                forceFire(target);
            }
            lastFire = now;
        }
    }

    public void forceFire(Vector2 target) {
        long now = System.currentTimeMillis();
        double rof = 1000d / (now - lastFire);  // Since now - lastFire is in millis.
        double damage = dps / rof;
        fire(target, damage);

    }

    public void onFire() {

    }

    @Override
    public String toString() {
        return name;
    }

    public static class ReloadAction extends GameAction {
        Weapon weapon;

        public ReloadAction(long dur, Weapon parent) {
            super(0, dur, 0);
            this.weapon = parent;
        }

        @Override
        public void onStart() {
            if (weapon.meter.reserve > 0 && weapon.doReloads) {
                weapon.reloading = true;
            } else {
                parent.stop();
            }
        }

        @Override
        public void onInterrupt() {
            weapon.reloading = false;
        }

        @Override
        public void tick() {
            if (weapon.ammo == weapon.magSize || !weapon.reloading || !weapon.doReloads) {
                // Wait! reload already finished. How the heck this could happen, idk but don't forget about edge cases.
                parent.stop();
            }
        }

        @Override
        public void onFinish() {
            weapon.reloading = false;
            int neededAmmo = weapon.magSize - weapon.ammo;
            if (neededAmmo <= weapon.meter.reserve) {
                weapon.meter.reserve -= neededAmmo;
                weapon.ammo += neededAmmo;
            } else {
                weapon.ammo += weapon.meter.reserve;
                weapon.meter.reserve = 0;
            }
        }
    }
}
