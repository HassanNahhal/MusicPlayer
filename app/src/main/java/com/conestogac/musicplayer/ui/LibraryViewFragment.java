package com.conestogac.musicplayer.ui;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
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
 * This fragement will display all libaries
 * author: Changho Choi
 */
public class LibraryViewFragment  extends Fragment {
    private final static String TAG="LibraryViewFragment";
    private static final String KEY_POSITION="position";
    private ListView listView;
    private Context ctxt;
    Cursor cursor;
    static LibraryViewFragment newInstance(int position) {
        LibraryViewFragment frag=new LibraryViewFragment();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }

    /**
     * Setup Gridlayout Manager and CardViewAdapter and Recycler View
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View result=inflater.inflate(R.layout.fragment_list_view, container, false);
        listView = (ListView) result.findViewById(R.id.listView);
        listView.setEmptyView(result.findViewById(R.id.empty_list_item));



        SongCursorAdapter rcAdapter = new SongCursorAdapter(ctxt, MusicHelper.getAllSongAsCursor(ctxt), 0);
        listView.setAdapter(rcAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Log.d(TAG, "onItemClick");

                // Get the cursor, positioned to the corresponding row in the result set
                cursor = (Cursor) listView.getItemAtPosition(position);

                String duration = MusicHelper.milliSecondsToTimer(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                Song selectedSong = new Song(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        duration);

                ArrayList <Song> songList = new ArrayList<>();
                songList.add(selectedSong);
                int FragmentPosition=getArguments().getInt(KEY_POSITION, 0);
                if (FragmentPosition == 1) {
                    Intent gotoMusicPlayer = new Intent(ctxt, PlayerActivity.class);
                    View sharedView = result.findViewById(R.id.albumArt);
                    String transitionName = ctxt.getString(R.string.albumart);

                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) ctxt, sharedView, transitionName);
                    gotoMusicPlayer.putExtra(PlayerActivity.EXTRA_SONGLIST, songList);
                    ctxt.startActivity(gotoMusicPlayer, transitionActivityOptions.toBundle());
                } else if(FragmentPosition == 5) {
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
}