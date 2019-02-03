package io.github.jgame.sprite;

import java.util.LinkedList;

public class Group {
    public LinkedList<Sprite> sprites = new LinkedList<>();

    public Group() {

    }

    public void add(Sprite sprite) {
        sprites.add(sprite);
        sprite.groups.add(this);
    }

    public void remove(Sprite sprite) {
        sprites.remove(sprite);
        sprite.groups.remove(this);
    }
}
