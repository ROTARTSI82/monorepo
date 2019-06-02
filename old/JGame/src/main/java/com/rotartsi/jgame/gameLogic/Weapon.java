package com.rotartsi.jgame.gameLogic;

import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.mechanics.BulletFactory;
import com.rotartsi.jgame.mechanics.PlatformerEntity;

/**
 * NOT YET IMPLEMENTED
 */
public class Weapon {

    public ActionManager manager = new ActionManager();

    public int ammo;
    public int magSize;
    public double dps;
    public int reserve;
    public boolean reloading = false;
    public double shotDamage;
    public long shotCooldown;
    public long lastFire = 0;
    public PlatformerEntity owner;
    public ReloadAction reloadAction;
    public BulletFactory factory;
    public String name = "Generic Weapon";

    /**
     *
     * @param dps Damage per second
     * @param rof Rate of Fire (Shots per second)
     * @param bullet Bullet Factory
     * @param owner Owner of the weapon
     * @param magSize Magazine size
     * @param reserve Ammo left
     * @param reloadDuration Reload time (miliseconds)
     */
    public Weapon(double dps, double rof, BulletFactory bullet, PlatformerEntity owner,
                  int magSize, int reserve, long reloadDuration) {
        shotDamage = dps / rof;
        this.dps = dps;
        factory = bullet;
        shotCooldown = (long) (1000 / rof);
        this.owner = owner;
        this.magSize = magSize;
        ammo = magSize;
        this.reserve = reserve;
        reloadAction = new ReloadAction(reloadDuration, this);
    }

    public void forceReload() {
        if (ammo < magSize && !reloading) {
            manager.doAction(reloadAction, true, true);
        }
        // Other two cases:  Already reloading or no need to reload.
    }

    public void tick() {
        if (ammo <= 0 && !reloading && reserve > 0) {
            manager.doAction(reloadAction, true, true);
        }
        manager.tick();
    }

    private void fire(Vector2 targetPos, double damage) {
        if (ammo <= 0) {
            return;
        }
        onFire();
        factory.addBullet(targetPos, damage, owner);
        ammo -= 1;
    }

    public void tryFire(Vector2 target) {
        long now = System.currentTimeMillis();
        if (now - lastFire > shotCooldown) {
            fire(target, shotDamage);
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
            if (weapon.reserve > 0) {
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
            if (weapon.ammo == weapon.magSize || !weapon.reloading) {
                // Wait! reload already finished. How the heck this could happen, idk but don't forget about edge cases.
                parent.stop();
            }
        }

        @Override
        public void onFinish() {
            weapon.reloading = false;
            int neededAmmo = weapon.magSize - weapon.ammo;
            if (neededAmmo <= weapon.reserve) {
                weapon.reserve -= neededAmmo;
                weapon.ammo += neededAmmo;
            } else {
                weapon.ammo += weapon.reserve;
                weapon.reserve = 0;
            }
        }
    }
}
