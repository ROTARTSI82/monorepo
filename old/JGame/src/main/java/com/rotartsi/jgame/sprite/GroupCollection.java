package com.rotartsi.jgame.sprite;

import java.util.LinkedList;

public class GroupCollection {

    public LinkedList<Group> groups = new LinkedList<>();


    public void add(Group g) {
        groups.add(g);
    }


    public void remove(Group g) {
        groups.remove(g);
    }
}
