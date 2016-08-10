package com.conestogac.musicplayer.ui;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Genre;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TagEditorActivity extends AppCompatActivity {

    int originalGenreId = -1;
    int genreId = -1;
    boolean isFirstTime = true;
    String genreFound;

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
        final EditText genre = (EditText) findViewById(R.id.genre);

        Spinner spinner;
        ArrayAdapter<CharSequence> adapter;
        int genreFoundPosition = 0;

        Bundle b = getIntent().getExtras();
        final Song currentSong = b.getParcelable("id");
        final ArrayList<Genre> genreList = MusicHelper.getGenreList(this.getBaseContext());


        artistName.setText(currentSong.getArtist());

        songTitle.setText(currentSong.getTitle());

        Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.AudioColumns._ID + " = ?";
        String arg[] = {String.valueOf(currentSong.getID())};
        String[] projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.TRACK


        };
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = getContentResolver().query(URI,projection ,selection, arg, sortOrder);


        if (cursor != null && cursor.moveToFirst()) {
            albumTitle.setText(cursor.getString(4));
            songYear.setText(cursor.getString(5));
            songTrack.setText(cursor.getString(7));
            songComposer.setText(cursor.getString(6));


            Button saveButton = (Button) findViewById(R.id.TagSaveButton);
            saveButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    updateData(currentSong);
                }
            });
        }

        final String[] genreNames = new String[genreList.size()];

        List<String> list = new ArrayList<String>();


        for (int j=0;j<genreList.size();j++) {
            ArrayList<Song> SongsByGenre =  MusicHelper.getSongListByGenre(this.getBaseContext(), genreList.get(j).getID());
            genreNames[j] = genreList.get(j).getGenre();
            list.add(genreList.get(j).getGenre());
            for (int k=0; k < SongsByGenre.size(); k++) {
                if (SongsByGenre.get(k).getID() == currentSong.getID()) {
                    genreFound = genreList.get(j).getGenre();
                    genre.setText(genreFound);

                    originalGenreId = genreList.get(j).getID();

                }
            }
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

                genreId = genreList.get(position).getID();

                if (isFirstTime) {
                    genre.setText(genreFound);
                    isFirstTime = false;

                }
                else {
                    genre.setText(genreList.get(position).getGenre());
                }

                //call calulcate method
                //calculatePrice();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


        getContentResolver().delete(MediaStore.Audio.Genres.Members.getContentUri(
                "external", originalGenreId), MediaStore.Audio.Genres.Members.AUDIO_ID + " = " + currentSong.getID(), null);

        //  getContentResolver().insert(MediaStore.Audio.Genres.Members.getContentUri(
        //        "external", genreId), MediaStore.Audio.Genres.Members.AUDIO_ID + " = " + currentSong.getID(), null);

        ContentValues values3 = new ContentValues();
        values3.put(MediaStore.Audio.Genres.Members.AUDIO_ID, currentSong.getID());
        getContentResolver().insert(MediaStore.Audio.Genres.Members.getContentUri("external", genreId),values3 );



        // ContentValues values2 = new ContentValues();
        //   String selection2 = MediaStore.Audio.AudioColumns._ID + " = ?";
        //   String arg2[] = {String.valueOf(currentSong.getID())};
        //   Uri musicUri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
        //  values2.put(MediaStore.Audio.Genres.Members.GENRE_ID, genreId);
        //  getContentResolver().update(musicUri, values2, selection2, arg2);


    }
}
