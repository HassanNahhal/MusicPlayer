package com.conestogac.musicplayer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Bitmap;

import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.ImageView;

import android.widget.ListView;
import android.widget.MediaController;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Author: Nicholas Collins
 * For showing how to use PlayService
 * Implement the MediaPlayerControl interface will be called when the user attempts to control playback
 */
public class PlayerActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private static final String TAG = "PlayerActivity";
    public static final String EXTRA_SONGLIST = "songlist";
    static private ArrayList<Song> songList;
    private ListView songView;
    private ImageView albumImage;
    private SlidingUpPanelLayout mLayout;

    //music service class
    private MusicService musicSrv;
    //intent to the service
    private Intent playIntent;
    // a flag to keep track of whether the Activity class is bound to the Service class or not
    private boolean musicBound=false;
    private MediaController controller;
    private boolean paused=false;
    private boolean playbackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
//        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        songView = (ListView)findViewById(R.id.song_list);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_SONGLIST)) {
            songList = (ArrayList<Song>) getIntent().getExtras().get(EXTRA_SONGLIST);
        }

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        albumImage = (ImageView) findViewById(R.id.albumImage);

        SongAdapter adapter = new SongAdapter(this, songList);
        songView.setAdapter(adapter);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        setupController();

        mLayout.setAnchorPoint(0.7f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
    }


    /**
     * connect to the service
     *
     */
    private ServiceConnection musicConnection = new ServiceConnection(){

        //callback methods will inform the class when the Activity instance has successfully connected to the Service instance
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    /**
     * start the Service instance when the Activity instance starts
     * Notice that we use the connection object we created so that
     * when the connection to the bound Service instance is made, we pass the song list.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    /**
     * Adding an onClick attribute to the layout for each item in the song list.
     */
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }

        albumImage =  (ImageView) findViewById(R.id.albumImage);
        albumImage.setImageBitmap(Bitmap.createScaledBitmap(musicSrv.currentArtwork, albumImage.getHeight(), albumImage.getWidth(), true));

        controller.show(0);
    }


    @Override
    protected void onPause(){
        super.onPause();
      }

    /**
     * This will ensure that the controller displays when the user returns to the app
     */
    @Override
    protected void onResume(){
        super.onResume();
    }


    /**
     * This will ensure that the controller displays when the user returns to the app
     */
    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    /**
     * Set up controller and set onClick on controller
     *
     */
    public void setupController(){
        //set the controller up
        controller = new MediaController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);

        //set
        controller.setAnchorView(findViewById(R.id.player_attach1));
        controller.setEnabled(true);
    }

    //play next
    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        controller.show(0);
        return false;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        //to avoid various exceptions that may occur when using the MediaPlayer and MediaController classes
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
         return 0;
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    /**
     * For creating Search Actionbar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Process Search Actionbar menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                if (musicSrv.setShuffle()) {
                    item.setIcon(R.drawable.ic_shuffle_white_24dp);
                } else {
                    item.setIcon(R.drawable.ic_trending_flat_white_24dp);
                }
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
