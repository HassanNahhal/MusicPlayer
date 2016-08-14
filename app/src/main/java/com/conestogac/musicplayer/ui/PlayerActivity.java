package com.conestogac.musicplayer.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
    private Point albumSize;

    private SlidingUpPanelLayout mLayout;

    //music service class
    private MusicService musicSrv;
    //intent to the service
    private Intent playIntent;
    // a flag to keep track of whether the Activity class is bound to the Service class or not
    private boolean musicBound=false;
    private MediaController musicController;
    private boolean paused=false;
    private boolean playbackPaused=false;

    //textswitcher to display song title
    private TextSwitcher textSwitcher;

    //local broadcast receiver to get message from service
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int songPos = intent.getIntExtra("songPos", 0);
            updateSongInfomation(songPos);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //set statusbar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));

        //get listview pointer which will have song list
        songView = (ListView)findViewById(R.id.song_list);

        //get songlist from intent and sort
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_SONGLIST)) {
            songList = (ArrayList<Song>) getIntent().getExtras().get(EXTRA_SONGLIST);
        }
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //get album image pointer
        albumImage = (ImageView) findViewById(R.id.albumImage);

        //set song adapter
        SongAdapter adapter = new SongAdapter(this, songList);
        songView.setAdapter(adapter);

        //setup sliding panel
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
        mLayout.setAnchorPoint(0.2f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        //setup text switcher viewer &animation
        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                TextView textview = new TextView(getApplicationContext());
                textview.setTextColor(Color.WHITE);
                textview.setBackgroundColor(Color.DKGRAY);
                textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                textview.setTypeface(null, Typeface.ITALIC);
                textview.setGravity(Gravity.CENTER);

                int padding_in_dp = 3;
                final float scale = getResources().getDisplayMetrics().density;
                int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

                textview.setPadding(0, 0, 0, padding_in_px);
                return textview;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);

        //resgister local broadcast receiver to get event from service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("songUpdated"));

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

            //music service bind, start player with 1st song
            songPicked(null);
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
        int songIndex;

        //if it is called at initial, set song index as 0
        if (view ==null) {
            songIndex = 0;
        } else {
            songIndex = Integer.parseInt(view.getTag().toString());
        }

        musicSrv.setSong(songIndex);
        musicSrv.playSong();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }

        updateSongInfomation(songIndex);
        musicController.show(0);
    }

    /**
     * To update song title at textswitcher and album art image
     */

    private void updateSongInfomation(int index) {
        if (musicSrv == null) {
            return;   //this is neeed, broadcast receiver is called when previous song is reset
        }

        if (musicBound && albumImage != null)
            albumImage.setImageBitmap(Bitmap.createScaledBitmap(musicSrv.currentArtwork, albumImage.getHeight(), albumImage.getWidth(), true));
        textSwitcher.setText(songList.get(index).getTitle());
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        musicController.show(0);
    }


    @Override
    protected void onPause(){
        super.onPause();
      }

    /**
     * This will ensure that the musicController displays when the user returns to the app
     */
    @Override
    protected void onResume(){
        super.onResume();
    }


    /**
     * This will ensure that the musicController displays when the user returns to the app
     */
    @Override
    protected void onDestroy() {
        if (musicBound) {
            unbindService(musicConnection);
            stopService(playIntent);
            musicSrv = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Set up musicController and set onClick on musicController
     *
     */
    public void setupController(){
        //set the musicController up
        musicController = new MediaController(this);
        musicController.setPrevNextListeners(new View.OnClickListener() {
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

        musicController.setMediaPlayer(this);

        //set musicController's anchor view
        musicController.setAnchorView(findViewById(R.id.dragView));
        musicController.setEnabled(true);
    }

    //play next
    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        musicController.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        musicController.show(0);
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
        if(musicSrv!=null && musicBound && musicSrv.isPlaying()) {
            return musicSrv.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPlaying();
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
        musicController.show(0);
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPlaying())
            return musicSrv.getDuration();
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
