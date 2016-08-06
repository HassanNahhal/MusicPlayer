package com.conestogac.musicplayer.ui;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Song;

import java.io.File;
import java.io.IOException;

public class TagEditorActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_editor);
        EditText artistName = (EditText) findViewById(R.id.artist);
        EditText albumTitle = (EditText) findViewById(R.id.albumTitle);
        EditText songTitle = (EditText) findViewById(R.id.title);
        EditText songYear = (EditText) findViewById(R.id.year);
        EditText songTrack = (EditText) findViewById(R.id.track);
        EditText songComposer = (EditText) findViewById(R.id.composer);

        Bundle b = getIntent().getExtras();
        final Song currentSong = b.getParcelable("id");


        artistName.setText(currentSong.getArtist());
        //albumTitle.setText(currentSong.getAlbumId());
        songTitle.setText(currentSong.getTitle());
        //songYear.setText(currentSong.g);
        //songTrack.setText(currentSong.);
        //songComposer.setText(currentSong);


        Button saveButton = (Button) findViewById(R.id.TagSaveButton);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                 updateData(currentSong);
            }
        });



    }

    public void updateData(Song currentSong) {
        EditText artistName = (EditText) findViewById(R.id.artist);
        EditText albumTitle = (EditText) findViewById(R.id.albumTitle);
        EditText songTitle = (EditText) findViewById(R.id.title);
        EditText songYear = (EditText) findViewById(R.id.year);
        EditText songTrack = (EditText) findViewById(R.id.track);
        EditText songComposer = (EditText) findViewById(R.id.composer);

        Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentValues values = new ContentValues();

        values.put(MediaStore.Audio.AudioColumns.TITLE, String.valueOf(songTitle.getText()));
        values.put(MediaStore.Audio.AudioColumns.ARTIST, String.valueOf(artistName.getText()));
        values.put(MediaStore.Audio.AudioColumns.ALBUM, String.valueOf(albumTitle.getText()) );
        values.put(MediaStore.Audio.AudioColumns.YEAR, String.valueOf(songYear.getText()));
        values.put(MediaStore.Audio.AudioColumns.TRACK, String.valueOf(songTrack.getText()));
        values.put(MediaStore.Audio.AudioColumns.COMPOSER, String.valueOf(songComposer.getText()));

        String selection = MediaStore.Audio.AudioColumns._ID + " = ?";
        String arg[] = {String.valueOf(currentSong.getID())};


        getContentResolver().update(URI, values, selection, arg);
    }
}
