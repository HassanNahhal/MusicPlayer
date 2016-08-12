package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Playlist;
import com.conestogac.musicplayer.util.DBHelper;

import java.util.ArrayList;

/**
 * Author: Hassan Nahhal
 */
public class PlayListViewFragment extends Fragment
        implements GetPlayListNameFragment.GetPlayListNameDialogListener, AdapterCallback {
    private final static String TAG="PlayListViewFragment";
    private static final String KEY_POSITION="position";
    private Context ctxt;
    private CardViewAdapter rcAdapter;
    private RecyclerView rView;
    private GridLayoutManager gridLayout;
    private ArrayList<Playlist> playLists;
    private DBHelper dbHelper;
    private FloatingActionButton fab;

    static PlayListViewFragment newInstance(int position) {
        PlayListViewFragment frag=new PlayListViewFragment();
        Bundle args=new Bundle();
        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);
        return(frag);
    }

    /**
     * Setup Gridlayout Manager and CardViewAdapter and Recycler View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recycler_view, container, false);

        //Todo consider sharing between some views by using POSITION information
        int position=getArguments().getInt(KEY_POSITION, 0);

        //setup grid layout with column 2
        gridLayout = new GridLayoutManager(getActivity(), 2);

        if (dbHelper == null) {
            dbHelper = new DBHelper(ctxt);
        }

        //connect recycler view with grid layout manager
        rView = (RecyclerView) view.findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gridLayout);

        playLists = dbHelper.getAllPlaylists();


        if (playLists != null) {
            //To reuse between view, use method overloading for constructor depends on view
            rcAdapter = new CardViewAdapter(playLists, this);
            rView.setAdapter(rcAdapter);
        }

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.show();

        //fab.setImageResource(R.drawable.ic_add);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Add Playlist");
                    GetPlayListNameFragment getPlayListNameFragment = GetPlayListNameFragment.newInstance("New PlayList Name");

                    getPlayListNameFragment.setTargetFragment(PlayListViewFragment.this, 300);
                    getPlayListNameFragment.show(getActivity().getSupportFragmentManager(), "fragment_edit_name");
                }
            });

        }
        return(view);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctxt = context;
        Log.d(TAG, "PlayListViewFragment Attach");
    }

    /**
     * This is called when the dialog is completed and the results have been passed
     */

    @Override
    public void onFinishGetPlayListNameDialog(String playlistName) {
        Log.d(TAG, "onFinishGetPlayListNameDialog");
        Playlist playlist_item = new Playlist(playlistName, 0);
        DBHelper dbHelper = new DBHelper(getContext());

        playlist_item.setID(dbHelper.insertPlaylist(playlist_item));
        playLists.add(playlist_item);  //although it is clear read from db, but to minimize garbage just adding at arraylist
        rcAdapter = new CardViewAdapter(playLists, this);
        rView.setAdapter(rcAdapter);
        rView.invalidate();
    }

    @Override
    public void onMethodCallback(int position) {
        Log.d(TAG, "onMethodCallback");
        //to minimize db operation and garbage
        playLists.remove(position);
        rcAdapter = new CardViewAdapter(playLists, this);
        rView.setAdapter(rcAdapter);
        rView.invalidate();
    }
}
