package com.conestogac.musicplayer.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Album;
import com.conestogac.musicplayer.model.Artist;
import com.conestogac.musicplayer.model.Playlist;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.DBHelper;
import com.conestogac.musicplayer.util.GlideUtil;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * CardViewAdapter
 * This class to list up AlbumView
 * which is used by display album, artist, genre
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private final static String TAG = "CardViewAdapter";
    private ArrayList<Integer> _id  = new ArrayList<>();
    private ArrayList<String> firstTitle = new ArrayList<>();
    private ArrayList<String> secondTitle  = new ArrayList<>();
    private ArrayList<String>  albumArt = new ArrayList<>();
    private ArrayList<Integer> number  = new ArrayList<>();
    private ArrayList<Song> songArrayList = new ArrayList<>();
    private int viewpagerPos = 0;
    private Context ctxt;

    private File file;

    //to send back event to fragement to update screen
    private AdapterCallback mAdapterCallback;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView overflow;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            overflow = (ImageView) v.findViewById(R.id.overflow);
        }
    }

    public CardViewAdapter(ArrayList<String> title, ArrayList<Integer> number, ArrayList<String> imageIds){
        this.firstTitle = title;
        this.secondTitle = title;
        this.number = number;
        this.albumArt = imageIds;
    }

    public CardViewAdapter(ArrayList<Album> albumList){
        for (Album album : albumList) {
            this._id.add(album.getID());
            this.firstTitle.add(album.getTitle());
            this.secondTitle.add(album.getArtist());
            this.number.add(album.getNumberOfSongs());
            this.albumArt.add(album.getAlbumArt());
            viewpagerPos = SlideViewPagerAdapter.ALBUM_VIEW;
        }
    }

    /**
     * To avoid Java's same using same erasure error, dummy is added
     */
    public CardViewAdapter(ArrayList<Artist> artistList, boolean dummy){
        for (Artist artist : artistList) {
            this._id.add(artist.getID());
            this.firstTitle.add(artist.getArtist());
            this.secondTitle.add(String.valueOf(artist.getNumberOfAlbums()) +
                    ((artist.getNumberOfAlbums() ==1)? " Album":" Albums" ));
            this.number.add(artist.getNumberOfSongs());
            //todo what image for artists
            this.albumArt.add(null);
        }
        viewpagerPos = SlideViewPagerAdapter.ARTIST_VIEW;
    }

    /**
     * Callback is  to give callback to fragment
     */
    public CardViewAdapter(ArrayList<Playlist> playList, AdapterCallback callback){
        this.mAdapterCallback = callback;
        for (Playlist list : playList) {
            this._id.add(list.getID());
            this.firstTitle.add(list.getName());
            this.secondTitle.add("");
            this.number.add(list.getNumberOfSong());
            //todo what images for playlist
            this.albumArt.add(null);
        }
        viewpagerPos = SlideViewPagerAdapter.PLAYLIST_VIEW;
    }


    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_album, parent, false);
        ctxt = parent.getContext();
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        final ImageView imageView = (ImageView)cardView.findViewById(R.id.albumArt);

        //if albumart exist, image is loaded by using Glide, otherwise it will maintain default image
        if (albumArt.get(position) != null) {
            file = new java.io.File(albumArt.get(position));
            GlideUtil.loadImageWithFilePath(file, imageView);
        }

        imageView.setContentDescription(firstTitle.get(position));
        TextView tvFirstTitle = (TextView)cardView.findViewById(R.id.firstTitle);
        TextView tvSecondTitle = (TextView)cardView.findViewById(R.id.secondTitle);
        TextView tvNumberOfSong = (TextView)cardView.findViewById(R.id.numberOfSong);
        final ImageView overflow = (ImageView) cardView.findViewById(R.id.overflow);
        tvFirstTitle.setText(firstTitle.get(position));
        tvSecondTitle.setText(secondTitle.get(position));
        if (viewpagerPos == SlideViewPagerAdapter.PLAYLIST_VIEW)
            tvSecondTitle.setVisibility(View.GONE);
        tvNumberOfSong.setText(String.valueOf(number.get(position)) + ((number.get(position) == 1)? " Song": " Songs"));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctxt = v.getContext();
                Log.d(TAG, "Click on Cardview "+position);

                // TODO:  Process OnClick on CardView: get position of click & call player with proper intent
                // set information
                // Transfer to musicplayer with intent

                switch (viewpagerPos){
                    case SlideViewPagerAdapter.ALBUM_VIEW:  //Get Album
                        songArrayList = MusicHelper.getSongListByAlbum(ctxt, _id.get(position));
                        break;
                    case SlideViewPagerAdapter.ARTIST_VIEW:  //Get Artist
                        songArrayList = MusicHelper.getSongListByArtist(ctxt, _id.get(position));
                        break;
                    case SlideViewPagerAdapter.PLAYLIST_VIEW:  //Get Playlist
                        songArrayList = new DBHelper(ctxt).getSongArrayListFromPlayList(_id.get(position));
                        break;
                }


                //to prevent music player from starting with zero songlist
                if (songArrayList.size() == 0)
                    return;

                Intent gotoMusicPlayer = new Intent(ctxt, PlayerActivity.class);
                View sharedView = imageView;
                String transitionName = ctxt.getString(R.string.albumart);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)ctxt, sharedView, transitionName);
                gotoMusicPlayer.putExtra(PlayerActivity.EXTRA_SONGLIST, songArrayList);
                ctxt.startActivity(gotoMusicPlayer, transitionActivityOptions.toBundle());
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DBHelper dbHelper = new DBHelper(v.getContext());
                if (viewpagerPos == SlideViewPagerAdapter.PLAYLIST_VIEW) {
                    dbHelper.deletePlaylist(_id.get(position));

                    try {
                        mAdapterCallback.onMethodCallback(position);
                    } catch (ClassCastException exception) {
                        // do something
                    }
                    return true;
                } else {
                    return false;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return firstTitle.size();
    }


    /**
     *
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(ctxt, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_playlist:
                    Toast.makeText(ctxt, "Add to playlist", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_remove_playlist:
                    Toast.makeText(ctxt, "Remove from playlist", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_edit_playlist:
                    Toast.makeText(ctxt, "Edit playlist", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
}
