package com.conestogac.musicplayer.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.conestogac.musicplayer.model.Playlist;
import com.conestogac.musicplayer.model.Song;

import java.util.ArrayList;

/**
 * Author: Changho Choi
 * This class is a helper to manage database
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "playlist.db";
    private static final int SCHEMA_VERSION = 1;
    private static final String TABLE_PLAYLIST = "playlist";
    private static final String TABLE_PLAYLIST_SONG = "playlistssong";
    private Context ctxt;

    //PlayList Table
    public static final String PLAYLIST_ID = "_id";
    public static final String PLAYLIST_NAME = "name";
    public static final String PLAYLIST_NUM_SONGS = "numberOfSong";

    //PlayList_Song Table
    public static final String PLAYLIST_SONG_FK_PLAYLIST_ID = "playlist_id";
    public static final String PLAYLIST_SONG_FK_SONG_ID = "song_id";

    //Define Create Table
    private static final String CREATE_TABLE_PLAYLIST =
            "CREATE TABLE "+ TABLE_PLAYLIST +
            "("+
            PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PLAYLIST_NAME + " TEXT NOT NULL, "+
            PLAYLIST_NUM_SONGS + " INTEGER DEFAULT 0 "+
            ")";
    private static final String CREATE_TABLE_PLAYLIST_SONG =
            "CREATE TABLE "+ TABLE_PLAYLIST_SONG +
            "("+
            PLAYLIST_SONG_FK_PLAYLIST_ID + " INTEGER NOT NULL, " +
            PLAYLIST_SONG_FK_SONG_ID+ " INTEGER NOT NULL" +
            ")";

      //constructor of helper class -> call super class(SQLiteOpenHelper)
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        ctxt = context;
    }

    //Create table with id autoincrement
    //Check name null and set mark default 0, to prevent garbage data into db
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYLIST);
        db.execSQL(CREATE_TABLE_PLAYLIST_SONG);
        this.insertDefaultPlaylist(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * To init Default Playlist
     * @param db
     */
    private void insertDefaultPlaylist(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(PLAYLIST_NAME, "Default");
        cv.put(PLAYLIST_NUM_SONGS, 0);
        db.insert(TABLE_PLAYLIST, null, cv);
    }

    //Playlist~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Insert playlist
    public Integer insertPlaylist(Playlist playlist) {
        Long retValue;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PLAYLIST_NAME, playlist.getName());
        cv.put(PLAYLIST_NUM_SONGS, playlist.getNumberOfSong());
        retValue = db.insert(TABLE_PLAYLIST, null, cv);

        db.close();
        return retValue.intValue();
    }

    //Get data using ID
    public Playlist getPlaylist(Integer id) {
        Playlist playlist;
        SQLiteDatabase db = getReadableDatabase();
        String sqlQuery =
                "SELECT * FROM " +
                        TABLE_PLAYLIST +
                    " WHERE _id=" + String.valueOf(id);

        Log.d(TAG, "getPlaylist() _ID: "+id);

        Cursor res = db.rawQuery(sqlQuery, null);
        res.moveToFirst();


        playlist = new Playlist(res.getInt(res.getColumnIndexOrThrow(PLAYLIST_ID)),
                                res.getString(res.getColumnIndexOrThrow(PLAYLIST_NAME)),
                                res.getInt(res.getColumnIndexOrThrow(PLAYLIST_NUM_SONGS)));

        //Close cursor, db
        res.close();
        db.close();
        return playlist;
    }


    //Update record with given ID
    public Integer updatePlaylist(Playlist playlist) {
        Integer numberOfUpdated;

        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        //to avoid update empty task
        //isEmpty should be used for string comparision
        if (playlist.getName().isEmpty() == false) {
            cv.put(PLAYLIST_NAME, playlist.getName());
        }

        //update data by search with ID
        numberOfUpdated = db.update(TABLE_PLAYLIST, cv, "_id = ? ",
                new String[]{Integer.toString(playlist.getId())});

        db.close();
        return numberOfUpdated;
    }

    //Delete playlist in the table with ID
    public Integer deletePlaylist(Integer id) {

        Integer ret_value;
        SQLiteDatabase db = getWritableDatabase();

        //delete playlist from playlist table
        db.delete(TABLE_PLAYLIST,
                "_id = ? ",
                new String[]{Integer.toString(id)});

        //delete all songs at playlistsong table
        ret_value = deleteAllSongsInThePlaylist(id);
        db.close();
        return ret_value;
    }


    //Get all list
    public ArrayList<Playlist> getAllPlaylists() {
        ArrayList<Playlist> playList = new ArrayList<>();
        int _idColumn;
        int nameColumn;
        int numberOfSongColumn;
        int thisId;
        String thisName;
        int thisNumberOfSong;

        //open database
        SQLiteDatabase db = this.getReadableDatabase();

        //Get cursor
        Cursor musicCursor = db.rawQuery("SELECT * FROM "+ TABLE_PLAYLIST, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            _idColumn = musicCursor.getColumnIndex
                    (DBHelper.PLAYLIST_ID);
            nameColumn = musicCursor.getColumnIndex
                    (DBHelper.PLAYLIST_NAME);
            numberOfSongColumn = musicCursor.getColumnIndex
                    (DBHelper.PLAYLIST_NUM_SONGS);

            //add album to list
            do {
                thisId = (int) musicCursor.getLong(_idColumn);
                thisName = musicCursor.getString(nameColumn);
                thisNumberOfSong = musicCursor.getInt(numberOfSongColumn);
                playList.add(new Playlist(thisId, thisName, thisNumberOfSong));
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }

        //close database
        db.close();
        return playList;
    }

    //Playlist_Song~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Insert playlist
    public Integer insertPlaylistSong(int playlist_id, int song_id) {
        Long retValue;
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PLAYLIST_SONG_FK_PLAYLIST_ID, playlist_id);
        cv.put(PLAYLIST_SONG_FK_SONG_ID, song_id);
        retValue = db.insert(TABLE_PLAYLIST_SONG, null, cv);

        db.close();
        return retValue.intValue();
    }

    public ArrayList<Song> getSongArrayListFromPlayList(Integer id) {
        ArrayList<Song> songList = new ArrayList<>();
        Playlist playlist;
        Song thisSong;
        int songId;

        Cursor musicCursor = getSonglistFromPlayListId(id);

        Log.d(TAG, "getPlaylist() _ID: "+id);
        if (musicCursor != null && musicCursor.getCount() > 0) {
            do {
                songId = musicCursor.getInt(musicCursor.getColumnIndex(DBHelper.PLAYLIST_SONG_FK_SONG_ID));
                thisSong = MusicHelper.getSongFromId(ctxt, songId);
                if (thisSong != null)
                    songList.add(thisSong);
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return songList;
    }

    //Get data using ID
    public Cursor getSonglistFromPlayListId(Integer id) {
        Playlist playlist;
        SQLiteDatabase db = getReadableDatabase();

        String sqlQuery =
                "SELECT * FROM " +
                        TABLE_PLAYLIST_SONG +
                        " WHERE " + PLAYLIST_SONG_FK_PLAYLIST_ID + "=" + String.valueOf(id);


        Log.d(TAG, "getPlaylist() _ID: "+id);

        Cursor res = db.rawQuery(sqlQuery, null);
        if (res != null)
            res.moveToFirst();

        db.close();
        return res;
    }

    //Delete playlist
    public void deletePlaylist(int playlist_id) {
        SQLiteDatabase db = getWritableDatabase();

        //delete ID
        db.delete(TABLE_PLAYLIST,
                "(" + PLAYLIST_ID + " = ?)",
                new String[]{Integer.toString(playlist_id)});

        db.delete(TABLE_PLAYLIST_SONG,
                "(" + PLAYLIST_SONG_FK_PLAYLIST_ID + " = ?)",
                new String[]{Integer.toString(playlist_id)});
        db.close();
    }

    //Delete playlist in the table with ID
    public Integer deleteSongFromPlaylist(int playlist_id, int song_id) {
        Integer ret_value;
        SQLiteDatabase db = getWritableDatabase();

        //delete ID

        ret_value = db.delete(TABLE_PLAYLIST_SONG,
                "(" + PLAYLIST_SONG_FK_PLAYLIST_ID + " = ? and "
                    + PLAYLIST_SONG_FK_SONG_ID + " = ?)",
                new String[]{Integer.toString(playlist_id),Integer.toString(song_id)});
        db.close();
        return ret_value;
    }

    //Delete all songs in the table with playlist id
    public Integer deleteAllSongsInThePlaylist(int playlist_id) {

        Integer ret_value;
        SQLiteDatabase db = getWritableDatabase();
        //delete ID
        ret_value = db.delete(TABLE_PLAYLIST_SONG,
                "(" + PLAYLIST_SONG_FK_PLAYLIST_ID + " = ?)",
                new String[]{Integer.toString(playlist_id)});
        db.close();
        return ret_value;
    }

    //Delete songs in the playlist with song id
    public Integer deleteSongFromPlayLists(int song_id) {

        Integer ret_value;
        SQLiteDatabase db = getWritableDatabase();

        //delete ID
        ret_value = db.delete(TABLE_PLAYLIST_SONG,
                PLAYLIST_SONG_FK_SONG_ID + "=? ",
                new String[]{Integer.toString(song_id)});
        db.close();
        return ret_value;
    }

    //process exception in case of upgrade of database which version will be managed by SCHEMA_VERSION
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_PLAYLIST);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_PLAYLIST_SONG);

        onCreate(db);
    }
}
