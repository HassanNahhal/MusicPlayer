package com.conestogac.musicplayer.model;

/**
 * This class will manage Album mode
 * author: Hassan Nahhal
 */
public class Album {
    private int id;
    private String title;
    private String artist;
    private String albumArt;
    private int numberOfSongs;

    public Album(int id, String title, String artist, String albumArt, int numberOfSongs) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
        this.numberOfSongs = numberOfSongs;
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }
}
