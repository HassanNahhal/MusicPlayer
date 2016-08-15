package com.conestogac.musicplayer.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.conestogac.musicplayer.R;

/**
 * To get playlist name
 * Author: Hassan Nahhal
 */
public class GetPlayListNameFragment  extends DialogFragment {
    static int updateIndex = -1;
    public GetPlayListNameFragment() {}

    public interface GetPlayListNameDialogListener {
        void onFinishGetPlayListNameDialogForAdd(String playlistName);
        void onFinishGetPlayListNameDialogForUpdate(String playlistName,int index);
    }

    public static GetPlayListNameFragment newInstance(String title) {
        updateIndex = -1;
        GetPlayListNameFragment frag = new GetPlayListNameFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
    public static GetPlayListNameFragment newInstance(String title, int index) {
        updateIndex = index;
        GetPlayListNameFragment frag = new GetPlayListNameFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_get_play_list_name, container);

        getDialog().setTitle(getArguments().getString("title", "No Title"));
        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);

        final EditText playlistName = (EditText) rootView.findViewById(R.id.playlistName);
        playlistName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GetPlayListNameDialogListener listener = (GetPlayListNameDialogListener) getTargetFragment();
                if (updateIndex == -1) {
                    listener.onFinishGetPlayListNameDialogForAdd(playlistName.getText().toString());
                } else {
                    listener.onFinishGetPlayListNameDialogForUpdate(playlistName.getText().toString(), updateIndex);
                }
                dismiss();
            }
        });

        return rootView;
    }

}
