package com.conestogac.musicplayer.model;

/**
 *  Created by Sungjoe Kim
 */
public class Genre {
    private int id;
    private String genre;

    public Genre(int id, String genre) {
        this.id = id;
        this.genre = genre;
    }

    public int getID() {
        return id;
    }

    public String getGenre() {
        return genre;
    }
}
