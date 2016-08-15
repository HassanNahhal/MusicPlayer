package com.conestogac.musicplayer.ui;

import android.content.Context;

import android.database.Cursor;

import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.util.MusicHelper;

import android.provider.MediaStore.Audio;
import android.widget.Toast;


/**
 * SongCursorAdapter
 * This is for displaying all song list
 * Author: Changho Choi
 */
public class SongCursorAdapter extends CursorAdapter {
    private static final String TAG = "SongCursorAdapter";
    private Context ctxt;
    private int viewPagePos;

    // Default constructor
    public SongCursorAdapter(Context context, Cursor cursor, int cflags) {
        super(context, cursor, 0);
        ctxt = context;
        viewPagePos = cflags;   //save pageviwer's position
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
        ImageView overflow = (ImageView) view.findViewById(R.id.overflow);
        String cur_duration = MusicHelper.milliSecondsToTimer(cursor.getLong(cursor.getColumnIndex(Audio.Media.DURATION)));

        ctxt = context;

        albumArt.setImageBitmap(MusicHelper.getArtAlbumFromAlbumId(context, cursor.getInt(cursor.getColumnIndex(Audio.Media.ALBUM_ID))));
        title.setText(cursor.getString(cursor.getColumnIndex(Audio.Media.TITLE)));
        artist.setText(cursor.getString(cursor.getColumnIndex(Audio.Media.ARTIST)));
        duration.setText(cur_duration);

        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        // In case of Tageditor, hide menu
        if (viewPagePos == SlideViewPagerAdapter.TAG_EDITOR)
            overflow.setVisibility(View.GONE);
    }
    /**
     *
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(ctxt, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_playlist:
                    Toast.makeText(ctxt, "Add to playlist", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
}
