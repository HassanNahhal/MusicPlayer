package com.conestogac.musicplayer.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
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
    private ArrayList<Integer> _ids = new ArrayList<>();
    private ArrayList<String> firstTitles = new ArrayList<>();
    private ArrayList<String> secondTitles = new ArrayList<>();
    private ArrayList<String> albumArts = new ArrayList<>();
    private ArrayList<Integer> numbers = new ArrayList<>();
    private ArrayList<Song> songArrayList = new ArrayList<>();
    private int viewpagerPos = 0;
    private Context ctxt;
    private Fragment playListFragment;

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
        this.firstTitles = title;
        this.secondTitles = title;
        this.numbers = number;
        this.albumArts = imageIds;
    }

    public CardViewAdapter(ArrayList<Album> albumList){
        for (Album album : albumList) {
            this._ids.add(album.getID());
            this.firstTitles.add(album.getTitle());
            this.secondTitles.add(album.getArtist());
            this.numbers.add(album.getNumberOfSongs());
            this.albumArts.add(album.getAlbumArt());
            viewpagerPos = SlideViewPagerAdapter.ALBUM_VIEW;
        }
    }

    /**
     * To avoid Java's same using same erasure error, dummy is added
     */
    public CardViewAdapter(ArrayList<Artist> artistList, boolean dummy){
        for (Artist artist : artistList) {
            this._ids.add(artist.getID());
            this.firstTitles.add(artist.getArtist());
            this.secondTitles.add(String.valueOf(artist.getNumberOfAlbums()) +
                    ((artist.getNumberOfAlbums() ==1)? " Album":" Albums" ));
            this.numbers.add(artist.getNumberOfSongs());
            //todo what image for artists
            this.albumArts.add(null);
        }
        viewpagerPos = SlideViewPagerAdapter.ARTIST_VIEW;
    }

    /**
     * Callback is  to give callback to fragment
     * This is for playlist
     */
    public CardViewAdapter(ArrayList<Playlist> playList, AdapterCallback callback, Fragment fragment){
        this.mAdapterCallback = callback;
        this.playListFragment = fragment;
        for (Playlist list : playList) {
            this._ids.add(list.getID());
            this.firstTitles.add(list.getName());
            this.secondTitles.add("");
            this.numbers.add(list.getNumberOfSong());
            //todo what images for playlist
            this.albumArts.add(null);
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
        if (albumArts.get(position) != null) {
            file = new java.io.File(albumArts.get(position));
            GlideUtil.loadImageWithFilePath(file, imageView);
        }

        imageView.setContentDescription(firstTitles.get(position));
        TextView tvFirstTitle = (TextView)cardView.findViewById(R.id.firstTitle);
        TextView tvSecondTitle = (TextView)cardView.findViewById(R.id.secondTitle);
        TextView tvNumberOfSong = (TextView)cardView.findViewById(R.id.numberOfSong);
        final ImageView overflow = (ImageView) cardView.findViewById(R.id.overflow);
        tvFirstTitle.setText(firstTitles.get(position));
        tvSecondTitle.setText(secondTitles.get(position));
        if (viewpagerPos == SlideViewPagerAdapter.PLAYLIST_VIEW)
            tvSecondTitle.setVisibility(View.GONE);
        tvNumberOfSong.setText(String.valueOf(numbers.get(position)) + ((numbers.get(position) == 1)? " Song": " Songs"));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctxt = v.getContext();
                Log.d(TAG, "Click on Cardview "+position);

                // set information
                // Transfer to musicplayer with intent

                switch (viewpagerPos){
                    case SlideViewPagerAdapter.ALBUM_VIEW:  //Get Album
                        songArrayList = MusicHelper.getSongListByAlbum(ctxt, _ids.get(position));
                        break;
                    case SlideViewPagerAdapter.ARTIST_VIEW:  //Get Artist
                        songArrayList = MusicHelper.getSongListByArtist(ctxt, _ids.get(position));
                        break;
                    case SlideViewPagerAdapter.PLAYLIST_VIEW:  //Get Playlist
                        songArrayList = new DBHelper(ctxt).getSongArrayListFromPlayList(_ids.get(position));
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
                showPopupMenu(holder.overflow, _ids.get(position), position);
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final DBHelper dbHelper = new DBHelper(v.getContext());
                if (viewpagerPos == SlideViewPagerAdapter.PLAYLIST_VIEW) {
                    //Get confirm to delete
                    new AlertDialog.Builder(ctxt)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle(R.string.delete_playlist)
                            .setMessage(ctxt.getString(R.string.message_to_confirm_delete))
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbHelper.deletePlaylist(_ids.get(position));
                                    try {
                                        mAdapterCallback.onMethodCallback(position);
                                    } catch (ClassCastException exception) {
                                        // do something
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return firstTitles.size();
    }


    /**
     *
     * Showing popup menu when tapping on 3 dots menu
     */
    private void showPopupMenu(View view, int _id, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(ctxt, view);
        MenuInflater inflater = popup.getMenuInflater();

        if (viewpagerPos == SlideViewPagerAdapter.PLAYLIST_VIEW)
            inflater.inflate(R.menu.menu_playlist, popup.getMenu());
        else
            inflater.inflate(R.menu.menu_album, popup.getMenu());

        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(_id, position));
        popup.show();
    }

    /**
     * Click listener class for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        //to save index for updating;
        private int _id;    //id of selection
        private int position;    //position of selection

        //to make it easy, position data is sent from the holder
        public MyMenuItemClickListener(int id, int position) {
            this._id = id;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_add_playlist) {         //adding to playlist
                switch (viewpagerPos) {
                    case SlideViewPagerAdapter.ALBUM_VIEW:  //Get Album
                        songArrayList = MusicHelper.getSongListByAlbum(ctxt, _id);
                        break;
                    case SlideViewPagerAdapter.ARTIST_VIEW:  //Get Artist
                        songArrayList = MusicHelper.getSongListByArtist(ctxt, _id);
                        break;
                    case SlideViewPagerAdapter.PLAYLIST_VIEW:  //Get Playlist
                        songArrayList = new DBHelper(ctxt).getSongArrayListFromPlayList(_id);
                        break;
                }
                //to prevent add 0 to songlist
                if (songArrayList.size() == 0)
                    return true;
                //set songlist and goto selecting playlist
                Intent gotoSelectPlaylist = new Intent(ctxt, SelectAndAddToPlayList.class);
                gotoSelectPlaylist.putExtra(PlayerActivity.EXTRA_SONGLIST, songArrayList);
                ctxt.startActivity(gotoSelectPlaylist);
                return true;
            } else if (menuItem.getItemId() == R.id.action_remove_song) {   //removing songs
                new AlertDialog.Builder(ctxt)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.delete_playlist)
                        .setMessage(ctxt.getString(R.string.message_to_confirm_delete))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DBHelper dbHelper = new DBHelper(ctxt);
                                switch (viewpagerPos) {
                                    case SlideViewPagerAdapter.ALBUM_VIEW:  //Get Album
                                        songArrayList = MusicHelper.getSongListByAlbum(ctxt, _id);
                                        break;
                                    case SlideViewPagerAdapter.ARTIST_VIEW:  //Get Artist
                                        songArrayList = MusicHelper.getSongListByArtist(ctxt, _id);
                                        break;
                                    case SlideViewPagerAdapter.PLAYLIST_VIEW:  //Get Playlist
                                        songArrayList = new DBHelper(ctxt).getSongArrayListFromPlayList(_id);
                                        dbHelper.deletePlaylist(_id);
                                        try {
                                            mAdapterCallback.onMethodCallback(position);
                                        } catch (ClassCastException exception) {
                                            // do something
                                        }
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            } else if (menuItem.getItemId() == R.id.action_edit_playlist) {
                    //call edit dialog fragement to get playlist name
                    GetPlayListNameFragment getPlayListNameFragment = GetPlayListNameFragment.newInstance("Edit PlayList Name", this._id);
                    getPlayListNameFragment.setTargetFragment(playListFragment, 300);
                    getPlayListNameFragment.show(playListFragment.getFragmentManager(), "fragment_edit_name");
                    return true;
            }

            return false;
        }
    }
}
