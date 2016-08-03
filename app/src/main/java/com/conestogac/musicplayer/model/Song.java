package com.conestogac.musicplayer.model;

/**
 * @author Changho Choi
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String albumId;

    public Song(long id, String title, String artist, String albumId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
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

    public String getAlbumId() { return albumId;}

}


