package com.conestogac.musicplayer.ui;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.MusicHelper;

import java.util.ArrayList;


/**
 * This fragement will display all libraries
 * author: Changho Choi
 */
public class LibraryViewFragment  extends Fragment implements AdapterCallback {
    private final static String TAG="LibraryViewFragment";
    private static final String KEY_POSITION="position";
    private ListView listView;
    private Context ctxt;
    private Cursor cursor;
    private SongCursorAdapter rcAdapter;
    static LibraryViewFragment newInstance(int position) {
        LibraryViewFragment frag=new LibraryViewFragment();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }

    /**
     * onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        //set up view
        final View result=inflater.inflate(R.layout.fragment_list_view, container, false);
        listView = (ListView) result.findViewById(R.id.listView);
        listView.setEmptyView(result.findViewById(R.id.empty_list_item));

        //read data from database and set adapter
        readDataFromDB(getArguments().getInt(KEY_POSITION, 0) + 1);

        //set OnClick Listener for list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Log.d(TAG, "onItemClick");

                // Get the cursor, positioned to the corresponding row in the result set
                cursor = (Cursor) listView.getItemAtPosition(position);
                String duration = MusicHelper.milliSecondsToTimer(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));

                //Set up song object for selected one to send to another activity
                //Activity can be player or tag editor depends on current viewpager
                Song selectedSong = new Song(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        duration);
                ArrayList <Song> songList = new ArrayList<>();
                songList.add(selectedSong);
                int FragmentPosition=getArguments().getInt(KEY_POSITION, 0) + 1;   //there's one difference between keyposition and view definition

                //This is needed to share fragement between Libraryview fragment and Tageditor fragmenet
                if (FragmentPosition == SlideViewPagerAdapter.LIBRARY_VIEW) {
                    Intent gotoMusicPlayer = new Intent(ctxt, PlayerActivity.class);
                    View sharedView = result.findViewById(R.id.albumArt);
                    String transitionName = ctxt.getString(R.string.albumart);
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) ctxt, sharedView, transitionName);
                    gotoMusicPlayer.putExtra(PlayerActivity.EXTRA_SONGLIST, songList);
                    ctxt.startActivity(gotoMusicPlayer, transitionActivityOptions.toBundle());
                } else if(FragmentPosition == SlideViewPagerAdapter.TAG_EDITOR) {
                    Bundle b = new Bundle();
                    b.putParcelable("id", selectedSong); //Your id
                    Intent gotoTagEditor = new Intent(ctxt, TagEditorActivity.class);
                    gotoTagEditor.putExtras(b);
                    ctxt.startActivity(gotoTagEditor);
                }
            }
        });
        return(result);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();

    }

    /**
     * Update Data by reading data from database and set adapter
     */
    private void updateData()
    {
        readDataFromDB(getArguments().getInt(KEY_POSITION, 0) + 1);
        listView.setAdapter(rcAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctxt = context;
        Log.d(TAG, "LibraryViewFragment Attach");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
        Database query can be a time consuming task ..
        so its safe to call database query in another thread
    */
    private void readDataFromDB(final int pagePos) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
            rcAdapter = new SongCursorAdapter(ctxt, MusicHelper.getAllSongAsCursor(ctxt), pagePos, LibraryViewFragment.this );
            listView.setAdapter(rcAdapter);
            }
        });
    }

    /**
     * Call back when playlist is removed which is called from adapter
     * eventbus pattern is used for sending event from adapter
     * @param position
     */
    @Override
    public void onMethodCallback(int position) {
        Log.d(TAG, "onMethodCallback");
        //to minimize db operation and garbage
        readDataFromDB(getArguments().getInt(KEY_POSITION, 0) + 1);
    }
}