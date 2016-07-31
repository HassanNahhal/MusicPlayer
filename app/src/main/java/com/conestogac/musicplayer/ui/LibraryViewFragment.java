package com.conestogac.musicplayer.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conestogac.musicplayer.R;

/**
 * This fragement will display all libaries
 * @author: Changho Choi
 */
public class LibraryViewFragment  extends Fragment {
    private final static String TAG="LibraryViewFragment";
    private static final String KEY_POSITION="position";
    private GridLayoutManager gridLayout;

    static LibraryViewFragment newInstance(int position) {
        LibraryViewFragment frag=new LibraryViewFragment();
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

        //setup Adapter and connect with recycler view
        String[] album = {"Test Album", "Test Album", "Test Album"};
        String[] songs = {"5 Songs","5 Songs","5 Songs"};
        int[] rId = {R.drawable.album_art,R.drawable.album_art,R.drawable.album_art};
        AlbumViewAdapter rcAdapter = new AlbumViewAdapter(album, songs, rId);
        rView.setAdapter(rcAdapter);

        return(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}