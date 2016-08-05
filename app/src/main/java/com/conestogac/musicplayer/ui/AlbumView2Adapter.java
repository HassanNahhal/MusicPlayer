package com.conestogac.musicplayer.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Album;
import com.conestogac.musicplayer.model.Artist;
import com.conestogac.musicplayer.model.Genre;
import com.conestogac.musicplayer.model.PlayList;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.GlideUtil;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * AlbumView2Adapter
 * Simplified Version of AlbumView Adapter
 * Just display Text and Image
 */
public class AlbumView2Adapter extends RecyclerView.Adapter<AlbumView2Adapter.ViewHolder> {
    private final static String TAG = "AlbumView2Adapter";
    private ArrayList<Integer> _id  = new ArrayList<Integer>();
    private ArrayList<String> firstTitle = new ArrayList<String>();
    private ArrayList<String>  albumArt = new ArrayList<String>();
    private ArrayList<Song> songArrayList = new ArrayList<Song>();

    private File file;
    private Listener listener;

    public interface Listener {
        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
        }
    }


    public AlbumView2Adapter(ArrayList<Genre> genreList){
        for (Genre genre : genreList) {
            this._id.add(genre.getID());
            this.firstTitle.add(genre.getGenre());
            //todo what image for genre
            this.albumArt.add("");
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public AlbumView2Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            GlideUtil.loadImage(file, imageView);
        }

        imageView.setContentDescription(firstTitle.get(position));
        TextView tvFirstTitle = (TextView)cardView.findViewById(R.id.firstTitle);
        tvFirstTitle.setText(firstTitle.get(position));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctxt = v.getContext();
                Log.d(TAG, "Click on Cardview "+position);
                Log.d(TAG, "Click on Cardview Listner "+listener);

                // TODO:  Process OnClick on CardView: get position of click & call player with proper intent
                // set information
                // Transfer to musicplayer with intent



                Intent gotoMusicPlayer = new Intent(ctxt, PlayListActivity.class);
                View sharedView = imageView;
                String transitionName = ctxt.getString(R.string.albumart);

                switch (CardViewPagerAdapter.curPosition){
                    case 3:  //Get Genre
                        songArrayList = MusicHelper.getSongListByGenre(ctxt, _id.get(position));
                        break;
                    case 5:  //Get PlayList
                        songArrayList = MusicHelper.getSongListByGenre(ctxt, _id.get(position));
                        break;
                }

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)ctxt, sharedView, transitionName);
                gotoMusicPlayer.putExtra(PlayListActivity.EXTRA_SONGLIST, songArrayList);
                ctxt.startActivity(gotoMusicPlayer, transitionActivityOptions.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return firstTitle.size();
    }
}
