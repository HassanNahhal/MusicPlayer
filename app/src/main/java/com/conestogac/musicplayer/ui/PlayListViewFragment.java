package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.os.Bundle;
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
 * Created by Hassan Nahhal
 */
public class PlayListViewFragment extends Fragment{
    private final static String TAG="PlayListViewFragment";
    private static final String KEY_POSITION="position";
    private Context ctxt;
    private GridLayoutManager gridLayout;
    private MusicHelper musicHelper;
    private ArrayList<Album> albumList;

    static PlayListViewFragment newInstance(int position) {
        PlayListViewFragment frag=new PlayListViewFragment();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);
        return(frag);
    }

    /**
     * Setup Gridlayout Manager and AlbumViewAdapter and Recycler View
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
        View result=inflater.inflate(R.layout.fragment_library_view, container, false);

        //Todo consider sharing between some views by using POSITION information
        int position=getArguments().getInt(KEY_POSITION, 0);

        //setup grid layout with column 2
        gridLayout = new GridLayoutManager(getActivity(), 2);

        //connect recycler view with grid layout manager
        RecyclerView rView = (RecyclerView) result.findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gridLayout);
        albumList = MusicHelper.getAlbumList(ctxt);

        if (albumList != null) {
            //To reuse between view, use method overloading for constructor depends on view
            AlbumViewAdapter rcAdapter = new AlbumViewAdapter(albumList);
            rView.setAdapter(rcAdapter);
        }
        return(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctxt = context;
        Log.d(TAG, "ArtistViewFragment Attach");
    }

}
