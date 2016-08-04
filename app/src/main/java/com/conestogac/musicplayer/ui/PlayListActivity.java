package com.conestogac.musicplayer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.MusicHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * For showing how to use PlayService
 * Implement the MediaPlayerControl interface will be called when the user attempts to control playback
 */
public class PlayListActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private ArrayList<Song> songList;
    private ListView songView;
    private MusicHelper musicUtil = new MusicHelper(this);
    //music service class
    private MusicService musicSrv;
    //intent to the service
    private Intent playIntent;
    // a flag to keep track of whether the Activity class is bound to the Service class or not
    private boolean musicBound=false;
    private MusicController controller;
    private boolean paused=false;
    private boolean playbackPaused=false;
    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        songView = (ListView)findViewById(R.id.song_list);
        songList = musicUtil.getSongList();
        setupController();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter adapter = new SongAdapter(this, songList);
        songView.setAdapter(adapter);
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
     * @param view
     */
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    /**
     * This will ensure that the controller displays when the user returns to the app
     */
    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setupController();
            paused=false;
        }
    }

    /**
     * This will ensure that the controller displays when the user returns to the app
     */
    //todo back key does not work
    @Override
    public void onBackPressed() {
        Intent gotoLibrary = new Intent(this, MainActivity.class);
        gotoLibrary.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(gotoLibrary);
    }

    /**
     * This will ensure that the controller displays when the user returns to the app
     */
    @Override
    protected void onDestroy() {
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
        controller = new MusicController(this);
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
        controller.setAnchorView(findViewById(R.id.song_list));
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
        //todo using percentage to move seekbar
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
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Process Search Actionbar menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                if (musicSrv.setShuffle() == true) {
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
            case R.id.action_search:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
