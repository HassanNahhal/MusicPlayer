package com.conestogac.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author Changho Choi
 */
public class Song  implements Parcelable {
    private long id;
    private String title;
    private String artist;
    private String albumId;
    private String duration;

    public Song(long id, String title, String artist, String albumId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.duration = "";
    }

    public Song(long id, String title, String artist, String albumId, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
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

    public String getAlbumId() { 
        return albumId;
    }

    public String getDuration() {
        return duration;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public Song(Parcel in){
        String[] data= new String[4];

        in.readStringArray(data);
        this.id = Integer.parseInt(data[0]);
        this.title = data[1];
        this.artist = data[2];
        this.albumId = data[3];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this.id), this.title,
                this.artist, this.albumId});
    }

    public static final Parcelable.Creator<Song> CREATOR= new Parcelable.Creator<Song>() {

        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);  //using parcelable constructor
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}


