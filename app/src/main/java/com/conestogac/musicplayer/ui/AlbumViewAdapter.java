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
import com.conestogac.musicplayer.model.PlayList;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.GlideUtil;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * AlbumViewAdapter
 * This class to list up AlbumView
 * which is used by display album, artist, genre
 */
public class AlbumViewAdapter extends RecyclerView.Adapter<AlbumViewAdapter.ViewHolder> {
    private final static String TAG = "AlbumViewAdapter";
    private ArrayList<Integer> _id  = new ArrayList<>();
    private ArrayList<String> firstTitle = new ArrayList<>();
    private ArrayList<String> secondTitle  = new ArrayList<>();
    private ArrayList<String>  albumArt = new ArrayList<>();
    private ArrayList<Integer> number  = new ArrayList<>();
    private ArrayList<Song> songArrayList = new ArrayList<>();

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

    public AlbumViewAdapter(ArrayList<String> title, ArrayList<Integer> number, ArrayList<String> imageIds){
        this.firstTitle = title;
        this.secondTitle = title;
        this.number = number;
        this.albumArt = imageIds;
    }

    public AlbumViewAdapter(ArrayList<Album> albumList){
        for (Album album : albumList) {
            this._id.add(album.getID());
            this.firstTitle.add(album.getTitle());
            this.secondTitle.add(album.getArtist());
            this.number.add(album.getNumberOfSongs());
            this.albumArt.add(album.getAlbumArt());
        }
    }

    /**
     * To avoid Java's same using same erasure error, dummy is added
     */
    public AlbumViewAdapter(ArrayList<Artist> artistList, boolean dummy){
        for (Artist artist : artistList) {
            this._id.add(artist.getID());
            this.firstTitle.add(artist.getArtist());
            this.secondTitle.add(String.valueOf(artist.getNumberOfAlbums()) +
                    ((artist.getNumberOfAlbums() ==1)? " Album":" Albums" ));
            this.number.add(artist.getNumberOfSongs());
            //todo what image for artists
            this.albumArt.add("");
        }
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
        final ImageView imageView = (ImageView)cardView.findViewById(R.id.albumArt);

        //if albumart exist, image is loaded by using Glide, otherwise it will maintain default image
        if (albumArt.get(position) != null) {
            file = new java.io.File(albumArt.get(position));
            GlideUtil.loadImage(file, imageView);
        }

        imageView.setContentDescription(firstTitle.get(position));
        TextView tvFirstTitle = (TextView)cardView.findViewById(R.id.firstTitle);
        TextView tvSecondTitle = (TextView)cardView.findViewById(R.id.secondTitle);
        TextView tvNumberOfSong = (TextView)cardView.findViewById(R.id.numberOfSong);
        tvFirstTitle.setText(firstTitle.get(position));
        tvSecondTitle.setText(secondTitle.get(position));
        tvNumberOfSong.setText(String.valueOf(number.get(position)) + ((number.get(position) == 1)? " Song": " Songs"));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctxt = v.getContext();
                Log.d(TAG, "Click on Cardview "+position);
                Log.d(TAG, "Click on Cardview Listner "+listener);

                // TODO:  Process OnClick on CardView: get position of click & call player with proper intent
                // set information
                // Transfer to musicplayer with intent

                switch (CardViewPagerAdapter.curPosition){
                    case 1:  //Get Album
                        songArrayList = MusicHelper.getSongListByAlbum(ctxt, _id.get(position));
                        break;
                    case 4:  //Get Artist
                        songArrayList = MusicHelper.getSongListByArtist(ctxt, _id.get(position));
                        break;
                }


                Intent gotoMusicPlayer = new Intent(ctxt, PlayListActivity.class);
                View sharedView = imageView;
                String transitionName = ctxt.getString(R.string.albumart);

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
