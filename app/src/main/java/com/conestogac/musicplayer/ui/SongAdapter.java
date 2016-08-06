package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;

import java.util.ArrayList;

/**
 *
 * use an Adapter to map the songs to the list view.
 *
 * author: Changho Choi
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    //We'll pass the song list from the main Activity class
    // and use the LayoutInflater to map the title and artist strings
    // to the TextViews in the song layout we created.
    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
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
        LinearLayout songPlay = (LinearLayout)songInf.inflate
                (R.layout.song_player, parent, false);
        //get title and artist views
        TextView songView = (TextView)songPlay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songPlay.findViewById(R.id.song_artist);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songPlay.setTag(position);
        return songPlay;
    }

}