package com.conestogac.musicplayer.model;

/**
 *  Created by: SungJoe Kim
 */
public class Artist {
    private int id;
    private String artist;
    private int numberOfAlbums;
    private int numberOfSongs;

    public Artist(int id, String artist, int numberOfAlbums, int numberOfSongs) {
        this.id = id;
        this.artist = artist;
        this.numberOfAlbums = numberOfAlbums;
        this.numberOfSongs = numberOfSongs;
    }

    public int getID() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }
    public int getNumberOfSongs() {
        return numberOfSongs;
    }
}
