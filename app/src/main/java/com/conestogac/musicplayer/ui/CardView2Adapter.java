package com.conestogac.musicplayer.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Genre;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.GlideUtil;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * CardView2Adapter
 * Simplified Version of AlbumView Adapter
 * Just display Text and Image
 */
public class CardView2Adapter extends RecyclerView.Adapter<CardView2Adapter.ViewHolder> {
    private final static String TAG = "CardView2Adapter";
    private ArrayList<Integer> _id  = new ArrayList<Integer>();
    private ArrayList<String> firstTitle = new ArrayList<String>();
    private ArrayList<String>  albumArt = new ArrayList<String>();
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    private int viewPagerPos = 0;
    private File file;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
        }
    }


    public CardView2Adapter(ArrayList<Genre> genreList){
        for (Genre genre : genreList) {
            this._id.add(genre.getID());
            this.firstTitle.add(genre.getGenre());
            //todo what image for genre
            this.albumArt.add(null);
        }
        viewPagerPos = 3;
    }


    @Override
    public CardView2Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_album2, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        final ImageView imageView = (ImageView)cardView.findViewById(R.id.albumArt);

        //if albumart exist, image is loaded by using Glide, otherwise it will maintain default image
        if (albumArt.get(position) != null) {
            file = new java.io.File(albumArt.get(position));
            GlideUtil.loadImageWithFilePath(file, imageView);
        } /*
            else {
            GlideUtil.loadProfileIcon(R.drawable.no_cover_small,imageView);
        }*/

        imageView.setContentDescription(firstTitle.get(position));
        TextView tvFirstTitle = (TextView)cardView.findViewById(R.id.firstTitle);
        tvFirstTitle.setText(firstTitle.get(position));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctxt = v.getContext();
                Log.d(TAG, "Click on Cardview "+position);

                // TODO:  Process OnClick on CardView: get position of click & call player with proper intent
                // set information
                // Transfer to musicplayer with intent

                Intent gotoMusicPlayer = new Intent(ctxt, PlayerActivity.class);
                View sharedView = imageView;
                String transitionName = ctxt.getString(R.string.albumart);

                switch (viewPagerPos){
                    case 3:  //Get Genre
                        songArrayList = MusicHelper.getSongListByGenre(ctxt, _id.get(position));
                        break;
                }

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)ctxt, sharedView, transitionName);
                gotoMusicPlayer.putExtra(PlayerActivity.EXTRA_SONGLIST, songArrayList);
                ctxt.startActivity(gotoMusicPlayer, transitionActivityOptions.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return firstTitle.size();
    }
}
