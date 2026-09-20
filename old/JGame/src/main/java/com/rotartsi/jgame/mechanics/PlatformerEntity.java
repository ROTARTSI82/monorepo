package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.sprite.GroupCollection;
import com.rotartsi.jgame.sprite.Sprite;

import java.util.LinkedList;

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public interface PlatformerEntity {
    double health = 0;

    GroupCollection collidables = new GroupCollection();

    void damage(double amount);

//    public void setBounds(ScreenBounds bounds);

    void update();

//    void checkBounds();
//
//    void clampVelocity();

    void onCollide(LinkedList<Sprite> collisions, String axis);
//    public void onCollide(LinkedList<Sprite> collisions, PlatformerEntity axis);
}
