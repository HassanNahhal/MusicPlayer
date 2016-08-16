package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Playlist;
import com.conestogac.musicplayer.model.Song;

import java.util.ArrayList;

/**
 * Playlist Adapter which is used in Player
 * This is customized adapter which have image and text at the list
 * And Data is given as Arraylist
 * Author Hassan Nahhal
 */
public class PlaylistAdapter extends BaseAdapter{
    private ArrayList<Playlist> playlists;
    private LayoutInflater playlistInf;

    //We'll pass the playlist list from the playlist fragment class
    // and use the LayoutInflater to map the name
    // to the TextViews in the playlist layout we created.
    public PlaylistAdapter(Context c, ArrayList<Playlist> thePlaylists){
        playlists = thePlaylists;
        playlistInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songPlay = (LinearLayout)playlistInf.inflate
                (R.layout.song_player, parent, false);
        //get title and artist views
        TextView songView = (TextView)songPlay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songPlay.findViewById(R.id.song_artist);
        //get song using position
        Playlist currPlaylist = playlists.get(position);
        //get title and artist strings
        songView.setText(currPlaylist.getName());
          //set position as tag
        songPlay.setTag(position);
        return songPlay;
    }
}
