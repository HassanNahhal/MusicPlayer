package com.conestogac.musicplayer.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.MusicHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * This service that plays background service
 * playing the music in the Service class, but
 * control it from the Activity class, where the application's user interface operates.
 *
 * author: Changho Choi
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private final static String TAG = "MusicService";
    //media player
    private MediaPlayer player;
    private static MusicService mInstance = null;
    private  Song playSong;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    //Binder
    private final IBinder musicBind = new MusicBinder();

    //play control
    private String songTitle="";

    //Notification
    NotificationManager mNotificationManager;
    Notification mNotification = null;
    private static final int NOTIFY_ID=1;


    //Shuffle
    private boolean shuffle=false;
    private Random rand;

    //current song's albumart
    public Bitmap currentArtwork;

    public MusicService() {
    }

    // Indicates the state our service:
    enum State {
        Idle,           // the MediaRetriever is retrieving music
        Innitialized,   // After reset()
        Preparing,      // media player is preparing...
        Prepared,       // media player is prepared
        Playing,        // playback active (media player ready!). (but the media player may actually be
                        // paused in this state if we don't have audio focus. But we stay in this state
                        // so that we know we have to resume playback once we get focus back)
        Paused,         // playback paused (media player ready!)
        Stopped,        // media player is stopped and not prepared to play
    }

    State mState = State.Idle;



    /**
     * create & init player service
     */
    public void onCreate(){
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        //init player
        initMusicPlayer();
        //for shuffle play
        rand=new Random();
        //set instance for getInstance
        mInstance = this;
        //to update notification,get notification manager
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    /*
    Return binding Object which will be use to send playList to service
    */
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    /*
        Release resources when the Service instance is unbound
        This will execute when the user exits the app, at which point, service should be stopped
     */
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    /**
     * init music player
     * Its very simple to use the media player to play audio files.
     * All we need to do is to initialize a media player object, set the audio stream,
     * prepare the audio for playback and then finally start the playback
     */
    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    /**
     * pass the list of song from Activity
     */
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
    //        mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "Playback Error");
        mp.reset();
        return false;
    }


    /**
     *  the interaction between the Activity and Service classes,
     *  for which we also need a Binder instance
     */
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    /**
     * Play Song
     * It will follow state machine defined at the following site
     * https://developer.android.com/reference/android/media/MediaPlayer.html
     */
    public void playSong(){
        //start by resetting the MediaPlayer
        // since we will also use this code when the user is playing subsequent songs
        player.reset();
        mState = State.Innitialized;


        //get the song from the list, extract the ID for it using its Song object, and model this as a URI
        playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
        long currSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        //URI as the data source for the MediaPlayer instance,
        //but an exception may be thrown if an error pops up so we use a try/catch block
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e(TAG, "Error setting data source", e);
        }

        //get album art
        currentArtwork = MusicHelper.getALbumArtFromSongID(this.getBaseContext(), playSong.getAlbumId());
        //send title to activity for updating song title & artwork
        sendSongUpdatedMessageToActivity();

        mState = State.Preparing;
        //calling the asynchronous method of the MediaPlayer to prepare it
        //When the MediaPlayer is prepared, the onPrepared method will be executed
        player.prepareAsync();
    }

    /**
     * When the MediaPlayer is prepared, the onPrepared method will be executed
     * It will start player
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback
        mState = State.Prepared;
        mediaPlayer.start();
        mState = State.Playing;

        //define pending intent which will be called when user select notification bar
        Intent notIntent = new Intent(this, PlayerActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //set up notification including message to be shown during the play
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_outline_black_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);

        //to update player's play/pause button. without this player shows play button firstly
        sendSongUpdatedMessageToActivity();
    }

    /**
     * return service instance
     */
    public static MusicService getInstance() {
        return mInstance;
    }

    /**
     * Get song index to play
     * this will be called when the user picks a song from the list
     */
    public void setSong(int songIndex){
        songPosn=songIndex;
    }


    /**
     * On completion or user press next, then this will be called
     * player reset should be called before call this
     */
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        } else {
            songPosn++;
            if(songPosn>=songs.size()) {
                songPosn=0;
            }
        }
        playSong();
    }

    /**
     * User press prev, then this will be called
     * player reset should be called before call this
     */
    public void playPrev(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }  else {
            songPosn--;
            if (songPosn < 0) {
                songPosn = songs.size() - 1;
            }
        }
        playSong();
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();

        //to update notification status as Pause
        Intent notIntent = new Intent(this, PlayerActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_outline_black_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Pause")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public boolean setShuffle(){
        shuffle = !shuffle;
        return shuffle;
    }

    public boolean getShuffle(){
        return shuffle;
    }

    /**
     * To send event to activity to update song title and album when song is updated within the service
     * This is Localbroadcast which is much simpler than global broadcast
     * https://developer.android.com/reference/android/support/v4/content/LocalBroadcastManager.html
     */
    private void sendSongUpdatedMessageToActivity() {
        Intent intent = new Intent("songUpdated");
        intent.putExtra("songPos", songPosn);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
