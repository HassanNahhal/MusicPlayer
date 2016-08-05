package com.conestogac.musicplayer.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    public Bitmap currentArtwork;

    public MusicService() {
    }

    // Indicates the state our service:
    enum State {
        Retrieving,     // the MediaRetriever is retrieving music
        Stopped,        // media player is stopped and not prepared to play
        Preparing,      // media player is preparing...
        Playing,        // playback active (media player ready!). (but the media player may actually be
                        // paused in this state if we don't have audio focus. But we stay in this state
                        // so that we know we have to resume playback once we get focus back)
        Paused          // playback paused (media player ready!)
    }

    State mState = State.Retrieving;



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
            mp.reset();
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
     *
     */
    public void playSong(){
        //start by resetting the MediaPlayer
        // since we will also use this code when the user is playing subsequent songs
        player.reset();

        //get the song from the list, extract the ID for it using its Song object, and model this as a URI
        Song playSong = songs.get(songPosn);
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

        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, Integer.parseInt(playSong.getAlbumId()));

        Context context = this.getBaseContext();

        Bitmap albumPicture = null;
        try {

            albumPicture = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), albumArtUri);


        } catch (FileNotFoundException exception) {
            albumPicture = BitmapFactory.decodeResource(getResources(), R.drawable.album_art);
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentArtwork = albumPicture;

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
        mediaPlayer.start();
        Intent notIntent = new Intent(this, PlayListActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_outline_black_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
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

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
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
}
