package com.conestogac.musicplayer.ui;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.DBHelper;
import com.conestogac.musicplayer.util.MusicHelper;

import android.provider.MediaStore.Audio;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * SongCursorAdapter
 * This is for displaying all song list which is used in PlaylisFragment
 * Author: SungJoe Kim
 */
public class SongCursorAdapter extends CursorAdapter {
    private static final String TAG = "SongCursorAdapter";
    private Context ctxt;
    private int viewPagePos;
    private ArrayList<Song> songArrayList = new ArrayList<>();
    //to send back event to fragement to update screen
    private AdapterCallback mAdapterCallback;

    /**
     * Constructor for adapter
     * @param context: context
     * @param cursor: cursor
     * @param cflags: to save viewPage position
     * @param callback: callback to send back to fragement
     */
    public SongCursorAdapter(Context context, Cursor cursor, int cflags, AdapterCallback callback) {
        super(context, cursor, 0);
        ctxt = context;
        viewPagePos = cflags;   //save pageviwer's position
        this.mAdapterCallback = callback;
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

        //set songID as TAG and read tag at onClick listener
        overflow.setTag(cursor.getLong(cursor.getColumnIndex(Audio.Media._ID)));
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int songIndex;
                //if it is called at initial, set song index as 0
                songIndex = Integer.parseInt(view.getTag().toString());
                Log.d(TAG, "setOnClickListener Tag: "+songIndex);
                showPopupMenu(view, songIndex);
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
    private void showPopupMenu(View view, int _id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(ctxt, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(_id));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        //to save index for updating;
        private int _id;

        //to make it easy, position data is sent from the holder
        public MyMenuItemClickListener(int id) {
            this._id = id;
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                //set songlist and goto selecting playlist
                case R.id.action_add_playlist:
                    songArrayList.add(MusicHelper.getSongFromId(ctxt, _id));
                    Intent gotoSelectPlaylist = new Intent(ctxt, SelectAndAddToPlayList.class);
                    gotoSelectPlaylist.putExtra(PlayerActivity.EXTRA_SONGLIST, songArrayList);
                    ctxt.startActivity(gotoSelectPlaylist);
                    return true;
                //Delete song from list. after deletion callback onMethodCallback is called to update view
                case R.id.action_remove_song:
                    new AlertDialog.Builder(ctxt)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle(R.string.delete_playlist)
                            .setMessage(ctxt.getString(R.string.message_to_confirm_delete))
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MusicHelper.deleteSongWithSongID(ctxt, _id);
                                    Toast.makeText(ctxt, "Deleted", Toast.LENGTH_SHORT).show();
                                    mAdapterCallback.onMethodCallback(0);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                default:
            }
            return false;
        }
    }
}
