package com.conestogac.musicplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.ui.MusicController;

import java.util.ArrayList;

/**
 * This class is for sharing API between classes
 * Create singleton to be able to access acroos the class
 *
 * @author Changho Choi
 */
public class MusicHelper {
    private Context ctxt;
    private ArrayList<Song> songList;
    private static MusicHelper singleton = null;

    synchronized static MusicHelper getInstance(Context ctxt) {
        if (singleton == null) {
            singleton = new MusicHelper(ctxt.getApplicationContext());
        }
        return (singleton);
    }

    //constructor of helper class -> call super class(SQLiteOpenHelper)
    public MusicHelper(Context context) {
        this.ctxt = context;
        songList = new ArrayList<Song>();
    }

    /**
     * Return song list as list with id, title, artist
     */
    public ArrayList<Song> getSongList() {
        ContentResolver musicResolver = ctxt.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //todo this will be added depends on list to be shown
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
                    //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        return songList;
    }

}
