package com.conestogac.musicplayer.model;

/**
 * @author Changho Choi
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String duration;

    public Song(long id, String title, String artist, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
    public String getDuration() {
        return duration;
    }
}


