package com.conestogac.musicplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.conestogac.musicplayer.R;

/**
 * Created by infomat on 16-07-29.
 */
public class LibraryViewFragment  extends Fragment {
    private static final String KEY_POSITION="position";

    static LibraryViewFragment newInstance(int position) {
        LibraryViewFragment frag=new LibraryViewFragment();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_library_view, container, false);
        EditText editor=(EditText)result.findViewById(R.id.editor);
        int position=getArguments().getInt(KEY_POSITION, -1);

        editor.setHint(String.format("hint", position + 1));

        return(result);
    }
}