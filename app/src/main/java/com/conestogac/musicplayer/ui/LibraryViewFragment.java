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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This fragement will display all libaries
 * author: Changho Choi
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
        ArrayList<String> album = new ArrayList<>(Arrays.asList("Buenos Aires", "Córdoba", "La Plata"));
        ArrayList<Integer> numberOfsongs = new ArrayList<>(Arrays.asList(4,5,6));
        ArrayList<String>  rId = new ArrayList<>(Arrays.asList("","",""));
        AlbumViewAdapter rcAdapter = new AlbumViewAdapter(album, numberOfsongs, rId);
        rView.setAdapter(rcAdapter);

        return(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "LibraryViewFragment Attach");
    }
}