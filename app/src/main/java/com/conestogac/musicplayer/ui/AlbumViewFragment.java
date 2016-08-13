package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Album;
import com.conestogac.musicplayer.util.MusicHelper;

import java.util.ArrayList;

/**
 * This fragement will display album view and each album will have a list of song for the album
 * author: Hassan Nahhal
 */
public class AlbumViewFragment extends Fragment {
    private final static String TAG="AlbumViewFragment";
    private static final String KEY_POSITION="position";
    private Context ctxt;
    private GridLayoutManager gridLayout;
    private MusicHelper musicHelper;
    private ArrayList<Album> albumList;
    private RecyclerView rView;
    private CardViewAdapter rcAdapter;

    static AlbumViewFragment newInstance(int position) {
        AlbumViewFragment frag=new AlbumViewFragment();
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
        View result=inflater.inflate(R.layout.fragment_recycler_view, container, false);

        //Todo consider sharing between some views by using POSITION information
        int position=getArguments().getInt(KEY_POSITION, 0);

        //setup grid layout with column 2
        gridLayout = new GridLayoutManager(getActivity(), 2);

        //connect recycler view with grid layout manager
        rView = (RecyclerView) result.findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gridLayout);

        //read data in separate thread
        readDataFromDB();

        return(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctxt = context;
        Log.d(TAG, "AlbumViewFragment Attach");
    }

    /**
     * Database query can be a time consuming task ..
     * so its safe to call database query in another thread
    */
    private void readDataFromDB() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
            albumList = MusicHelper.getAlbumList(ctxt);

            if (albumList != null) {
                //To reuse between view, use method overloading for constructor depends on view
                rcAdapter = new CardViewAdapter(albumList);
                rView.setAdapter(rcAdapter);
            }
            }
        });
    }

}
