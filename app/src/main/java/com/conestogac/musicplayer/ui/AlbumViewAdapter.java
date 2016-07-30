package com.conestogac.musicplayer.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.conestogac.musicplayer.R;

/**
 * Created by infomat on 16-07-28.
 */
public class AlbumViewAdapter extends RecyclerView.Adapter<AlbumViewAdapter.ViewHolder> {
    private String[] title;
    private String[] number;
    private int[] imageIds;
    private Listener listener;

    public static interface Listener {
        public void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
        }
    }

    public AlbumViewAdapter(String[] title, String[] number, int[] imageIds){
        this.title = title;
        this.number = number;
        this.imageIds = imageIds;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public AlbumViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_album, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView)cardView.findViewById(R.id.albumArt);
        Drawable drawable = cardView.getResources().getDrawable(imageIds[position]);
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(title[position]);
        TextView tvSongTitle = (TextView)cardView.findViewById(R.id.songTitle);
        TextView tvNumberOfSong = (TextView)cardView.findViewById(R.id.numberOfSong);
        tvSongTitle.setText(title[position]);
        tvNumberOfSong.setText(number[position]);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                    // TODO:  Fill out

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.length;
    }
}
