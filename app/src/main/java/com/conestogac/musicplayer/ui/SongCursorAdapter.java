package com.conestogac.musicplayer.ui;

import android.content.Context;

import android.database.Cursor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.util.MusicHelper;

import android.provider.MediaStore.Audio;


/**
 * SongCursorAdapter
 * This is for displaying all song list
 * Author: Changho Choi
 */
public class SongCursorAdapter extends CursorAdapter {
    private static final String TAG = "SongCursorAdapter";
    private Context ctxt;

    // Default constructor
    public SongCursorAdapter(Context context, Cursor cursor, int cflags) {
        super(context, cursor, 0);
        ctxt = context;
    }


    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, final ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.song_all_list, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the data on a TextView.
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView albumArt = (ImageView) view.findViewById(R.id.albumArt) ;
        TextView title = (TextView) view.findViewById(R.id.song_title);
        TextView artist = (TextView) view.findViewById(R.id.song_artist);
        TextView duration = (TextView) view.findViewById(R.id.song_duration);
        String cur_duration = MusicHelper.milliSecondsToTimer(cursor.getLong(cursor.getColumnIndex(Audio.Media.DURATION)));

        ctxt = context;

        albumArt.setImageBitmap(MusicHelper.getArtAlbumFromAlbumId(context, cursor.getInt(cursor.getColumnIndex(Audio.Media.ALBUM_ID))));
        title.setText(cursor.getString(cursor.getColumnIndex(Audio.Media.TITLE)));
        artist.setText(cursor.getString(cursor.getColumnIndex(Audio.Media.ARTIST)));
        duration.setText(cur_duration);
    }

}
