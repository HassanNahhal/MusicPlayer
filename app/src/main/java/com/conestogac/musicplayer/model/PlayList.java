package com.conestogac.musicplayer.model;

/**
 * Created by infomat on 16-07-28.
 */
public class PlayList {
    private int id;
    private String name;
    public PlayList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
