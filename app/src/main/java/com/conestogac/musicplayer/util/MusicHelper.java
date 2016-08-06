package com.conestogac.musicplayer.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.support.annotation.NonNull;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Album;
import com.conestogac.musicplayer.model.Artist;
import com.conestogac.musicplayer.model.Genre;
import com.conestogac.musicplayer.model.Song;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is for sharing API between classes
 * vis Static method
 *
 * author Changho Choi
 */
public class MusicHelper {

    /**
     * Return song all list
     */
    static public Cursor getAllSongAsCursor(Context ctxt) {
        String[] projection = new String[]{Audio.Media._ID, Audio.Media.TITLE, Audio.Media.ARTIST, Audio.Media.ALBUM_ID, Audio.Media.DURATION};
        String sortOrder = Audio.Media.TITLE + " ASC";

        ContentResolver musicResolver = ctxt.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, projection, null, null, sortOrder);

        return musicCursor;
    }


    /**
     * Return song all list
     */
    static public ArrayList<Song> getAllSongList(Context ctxt) {
        String[] projection = new String[]{Audio.Media._ID, Audio.Media.TITLE, Audio.Media.ARTIST, Audio.Media.ALBUM_ID, Audio.Media.DURATION};
        String sortOrder = Audio.Media.TITLE + " ASC";

        ContentResolver musicResolver = ctxt.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, projection, null, null, sortOrder);

        return getSongListAsArrayList(musicCursor);
    }

    /**
     * Return song all list
     */
    static public ArrayList<Song> getSongListByAlbum(Context ctxt, int albumId) {

        String[] projection = new String[]{Audio.Media._ID, Audio.Media.TITLE, Audio.Media.ARTIST, Audio.Media.ALBUM_ID};
        String   selection = Audio.Media.ALBUM_ID + "=?";
        String[] selectionArgs = {String.valueOf(albumId)};
        String sortOrder = Audio.Media.TITLE + " ASC";

        ContentResolver musicResolver = ctxt.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, projection, selection, selectionArgs, sortOrder);

        return getSongListAsArrayList(musicCursor);
    }

    /**
     * Return song by Artist
     */
    static public ArrayList<Song> getSongListByArtist(Context ctxt, int artistId) {

        String[] projection = new String[]{Audio.Media._ID, Audio.Media.TITLE, Audio.Media.ARTIST, Audio.Media.ALBUM_ID};
        String   selection = Audio.Media.ARTIST_ID + "=?";
        String[] selectionArgs = {String.valueOf(artistId)};
        String sortOrder = Audio.Media.TITLE + " ASC";

        ContentResolver musicResolver = ctxt.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, projection, selection, selectionArgs, sortOrder);

        return getSongListAsArrayList(musicCursor);
    }

    /*
        Internal method to make array list of Song from the input cursor
        This is shared between returning songlist by all song, album, by genre, by artist
     */
    @NonNull
    static private ArrayList<Song> getSongListAsArrayList(Cursor musicCursor) {
        long thisId;
        String thisTitle;
        String thisArtist;
        String thisAlbumId;

        ArrayList<Song> songList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //todo this will be added depends on list to be shown
            //get columns
            int idColumn = musicCursor.getColumnIndex
                    (Audio.Media._ID);
            int titleColumn = musicCursor.getColumnIndex
                    (Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (Audio.Media.ARTIST);
            int albumIdColumn = musicCursor.getColumnIndex
                    (Audio.Albums.ALBUM_ID);
            do {
                thisId = musicCursor.getLong(idColumn);
                thisTitle = musicCursor.getString(titleColumn);
                thisArtist = musicCursor.getString(artistColumn);
                thisAlbumId = musicCursor.getString(albumIdColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumId));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return songList;
    }

    /**
     * Return song by Genre
     */
    static public ArrayList<Song> getSongListByGenre(Context ctxt, int genreId) {

        String[] projection = new String[]{Audio.Genres.Members.AUDIO_ID, Audio.Genres.Members.TITLE,
        Audio.Genres.Members.ARTIST, Audio.Genres.Members.ALBUM_ID};
        String sortOrder = Audio.Media.TITLE + " ASC";

        ContentResolver musicResolver = ctxt.getContentResolver();
        Uri musicUri = Audio.Genres.Members.getContentUri("external", genreId);

        Cursor musicCursor = musicResolver.query(musicUri, projection, null, null, sortOrder);

        return getSongListAsArrayList2(musicCursor);
    }


    /*
        Internal method to make array list of Song from the input cursor
        This is shared between returning songlist by all song, album, by genre, by artist
     */
    @NonNull
    static private ArrayList<Song> getSongListAsArrayList2(Cursor musicCursor) {
        long thisId;
        String thisTitle;
        String thisArtist;
        String thisAlbumId;

        ArrayList<Song> songList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //todo this will be added depends on list to be shown
            //get columns
            int idColumn = musicCursor.getColumnIndex
                    (Audio.Genres.Members.AUDIO_ID);
            int titleColumn = musicCursor.getColumnIndex
                    (Audio.Genres.Members.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (Audio.Genres.Members.ARTIST);
            int albumIdColumn = musicCursor.getColumnIndex
                    (Audio.Genres.Members.ALBUM_ID);
            do {
                thisId = musicCursor.getLong(idColumn);
                thisTitle = musicCursor.getString(titleColumn);
                thisArtist = musicCursor.getString(artistColumn);
                thisAlbumId = musicCursor.getString(albumIdColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumId));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return songList;
    }

    /**
     * Return album list as String
     */
    static public ArrayList<Album> getAlbumList(Context ctxt) {
        ArrayList<Album> albumList = new ArrayList<>();
        ContentResolver musicResolver = ctxt.getContentResolver();
        String[] projection = new String[]{Audio.Albums._ID, Audio.Albums.ALBUM, Audio.Albums.ARTIST, Audio.Albums.ALBUM_ART, Audio.Albums.NUMBER_OF_SONGS};
        String sortOrder = Audio.Media.ALBUM + " ASC";

        Uri albumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(albumUri, projection, null, null, sortOrder);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int _idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums._ID);
            int albumColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ALBUM);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ARTIST);
            int albumArtColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ALBUM_ART);
            int numberOfSongsColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS);

            //add album to list
            do {
                int thisId = (int) musicCursor.getLong(_idColumn);
                String thisTitle = musicCursor.getString(albumColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbumArt = musicCursor.getString(albumArtColumn);
                int thisNumberOfSongs = musicCursor.getInt(numberOfSongsColumn);

                albumList.add(new Album(thisId, thisTitle, thisArtist, thisAlbumArt, thisNumberOfSongs));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return albumList;
    }

    /**
     * Return Artist list as String
     */
    static public ArrayList<Artist> getArtistList(Context ctxt) {
        ArrayList<Artist> artistList = new ArrayList<>();
        ContentResolver musicResolver = ctxt.getContentResolver();
        String[] projection = new String[]{Audio.Artists._ID, Audio.Artists.ARTIST,
                Audio.Artists.NUMBER_OF_ALBUMS, Audio.Artists.NUMBER_OF_TRACKS};
        String sortOrder = Audio.Media.ARTIST + " ASC";

        Uri artistUri = Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(artistUri, projection, null, null, sortOrder);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int _idColumn = musicCursor.getColumnIndex
                    (Audio.Artists._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (Audio.Artists.ARTIST);
            int numberOfAlbumColumn = musicCursor.getColumnIndex
                    (Audio.Artists.NUMBER_OF_ALBUMS);
            int numberOfTrackColumn = musicCursor.getColumnIndex
                    (Audio.Artists.NUMBER_OF_TRACKS);

            //add album to list
            do {
                int thisId = (int) musicCursor.getLong(_idColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int thisNumberOfAlbum = musicCursor.getInt(numberOfAlbumColumn);
                int thisNumberOfTrack = musicCursor.getInt(numberOfTrackColumn);
                artistList.add(new Artist(thisId, thisArtist, thisNumberOfAlbum,thisNumberOfTrack));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return artistList;
    }

    /**
     * Return Artist list as String
     */
    static public ArrayList<Genre> getGenreList(Context ctxt) {
        ArrayList<Genre> genreList = new ArrayList<>();
        ContentResolver musicResolver = ctxt.getContentResolver();
        String[] projection = new String[]{Audio.Genres._ID, Audio.Genres.NAME};
        String[] countPrj = new String[]{Audio.Genres.Members.AUDIO_ID};
        String sortOrder = Audio.Genres.NAME + " ASC";

        int _idColumn;
        int nameColumn;
        int thisId;
        String thisName;
        Cursor genreCursor;

        Uri artistUri = Audio.Genres.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(artistUri, projection, null, null, sortOrder);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            _idColumn = musicCursor.getColumnIndex
                    (Audio.Genres._ID);
            nameColumn = musicCursor.getColumnIndex
                    (Audio.Genres.NAME);

            //add album to list
            do {
                thisId = (int) musicCursor.getLong(_idColumn);
                Uri genreMemberUri = Audio.Genres.Members.getContentUri("external", thisId);
                if (musicResolver.query(genreMemberUri, countPrj, null, null, null).getCount() == 0)
                    continue;

                thisName = musicCursor.getString(nameColumn);
                genreList.add(new Genre(thisId, thisName));
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return genreList;
    }

    /**
     * getArtAlbumFromAlbumId
     * retun bitmap of albumart from albumId
     */
    static public Bitmap getArtAlbumFromAlbumId(Context context, int albumId) {
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
        Bitmap albumPicture = null;

        try {

            albumPicture = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), albumArtUri);


        } catch (FileNotFoundException exception) {
            albumPicture = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_art);
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return albumPicture;
    }


     /**
     * Function to convert milliseconds time to Timer Format
     * Hours:Minutes:Seconds
     * */
    static public  String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

}


