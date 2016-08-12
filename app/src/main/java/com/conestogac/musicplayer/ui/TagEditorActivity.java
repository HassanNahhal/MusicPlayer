package com.conestogac.musicplayer.ui;

//Written by N. Collins

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.conestogac.musicplayer.util.GlideUtil;


import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Genre;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TagEditorActivity extends AppCompatActivity {


    private Uri mFileUri = null;

    int originalGenreId = -1;
    int genreId = -1;

    String genreFound;
    Song currentSong;

    private static final int RESULT_LOAD_IMG = 2;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private Uri mDownloadUrl = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Get the control objects we need to use
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_editor);
        EditText artistName = (EditText) findViewById(R.id.artist);
        EditText albumTitle = (EditText) findViewById(R.id.albumTitle);
        EditText songTitle = (EditText) findViewById(R.id.title);
        EditText songYear = (EditText) findViewById(R.id.year);
        EditText songTrack = (EditText) findViewById(R.id.track);
        EditText songComposer = (EditText) findViewById(R.id.composer);


        //Genre can be updated by a spinner
        Spinner genreSpinner;
        genreSpinner = (Spinner) findViewById(R.id.spinner);

        Bundle b = getIntent().getExtras();
        currentSong = b.getParcelable("id");
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

        Cursor cursor = getContentResolver().query(URI, projection, selection, arg, sortOrder);


        if (cursor != null && cursor.moveToFirst()) {
            albumTitle.setText(cursor.getString(4));
            songYear.setText(cursor.getString(5));
            songTrack.setText(cursor.getString(7));
            songComposer.setText(cursor.getString(6));


            Button saveButton = (Button) findViewById(R.id.TagSaveButton);
            saveButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    updateData(currentSong);
                    Toast.makeText(TagEditorActivity.this, "Saved !!!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        final String[] genreNames = new String[genreList.size()];

        List<String> list = new ArrayList<String>();
        int originalGenreIndex = 0;

        for (int j = 0; j < genreList.size(); j++) {
            ArrayList<Song> SongsByGenre = MusicHelper.getSongListByGenre(this.getBaseContext(), genreList.get(j).getID());
            genreNames[j] = genreList.get(j).getGenre();
            list.add(genreList.get(j).getGenre());
            for (int k = 0; k < SongsByGenre.size(); k++) {
                if (SongsByGenre.get(k).getID() == currentSong.getID()) {
                    genreFound = genreList.get(j).getGenre();
                    genreSpinner.setSelection(genreList.get(j).getID());
                    originalGenreId = genreList.get(j).getID();
                    originalGenreIndex = j;
                }
            }
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(spinnerArrayAdapter);

        genreSpinner.setSelection(originalGenreIndex);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id)
            {
                genreId = genreList.get(position).getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        final ImageView albumImage2 =  (ImageView) findViewById(R.id.tagViewAlbumImage);
        ViewTreeObserver vto = albumImage2.getViewTreeObserver();

        final Context context = this.getBaseContext();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                albumImage2.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // Get the width and height
                //int width  = albumImage.getMeasuredWidth();
                int width = albumImage2.getWidth();
                //int height = albumImage.getMeasuredHeight();
                int height = albumImage2.getHeight();

                Bitmap currentAlbumArt = MusicHelper.getArtAlbumFromAlbumId(context, Integer.parseInt(currentSong.getAlbumId()));
                // Here you can get the dimensions
              //albumImage.setImageBitmap(currentAlbumArt);
                albumImage2.setBackground(null);
                //purposely set height to width to create a square view that fills the ImageView
                albumImage2.setImageBitmap(Bitmap.createScaledBitmap(currentAlbumArt, width, width, true));
            }
        });

        ImageView albumImage = (ImageView) findViewById(R.id.tagViewAlbumImage);
        albumImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // do stuff
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // After selecting a photo from gallery
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && null != data) {
            // Get the Image from data user selected
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            // Using image file path, set selected image on imageView element
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            String mCurrentPhotoPath = imgDecodableString;
            galleryAddPic(mCurrentPhotoPath);
            //Set image on the ImageView element
            setImageView();
        }
    }
    // Binding image on image View element using Picasso open source component
    private void setImageView(){
        if (mFileUri != null) {
            ImageView imageView = (ImageView) findViewById(R.id.tagViewAlbumImage);
            GlideUtil.loadImageWithUrl(mFileUri.toString(),imageView);
        }
    }
    // return the file path selected image using Intent
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);

        mFileUri = Uri.fromFile(f);
        mediaScanIntent.setData(mFileUri);
        this.sendBroadcast(mediaScanIntent);
    }


    /*
    @Override
    protected void onResume()
    {
        super.onResume();
        final ImageView albumImage =  (ImageView) findViewById(R.id.tagViewAlbumImage);
        Bitmap currentAlbumArt = MusicHelper.getArtAlbumFromAlbumId(this.getBaseContext(), Integer.parseInt(currentSong.getAlbumId()));
        // Here you can get the dimensions
        albumImage.setImageBitmap(Bitmap.createScaledBitmap(currentAlbumArt, 200, 200, true));
    }
    */

    //After user clicks save, we need to save the updates
    public void updateData(Song currentSong) {

        //Get all the data on screen
        EditText artistName = (EditText) findViewById(R.id.artist);
        EditText albumTitle = (EditText) findViewById(R.id.albumTitle);
        EditText songTitle = (EditText) findViewById(R.id.title);
        EditText songYear = (EditText) findViewById(R.id.year);
        EditText songTrack = (EditText) findViewById(R.id.track);
        EditText songComposer = (EditText) findViewById(R.id.composer);

        //Get a URI object we can use to save updates
        Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();

        //Set up all the values we need to save
        values.put(MediaStore.Audio.AudioColumns.TITLE, String.valueOf(songTitle.getText()));
        values.put(MediaStore.Audio.AudioColumns.ARTIST, String.valueOf(artistName.getText()));
        values.put(MediaStore.Audio.AudioColumns.ALBUM, String.valueOf(albumTitle.getText()) );
        values.put(MediaStore.Audio.AudioColumns.YEAR, String.valueOf(songYear.getText()));
        values.put(MediaStore.Audio.AudioColumns.TRACK, String.valueOf(songTrack.getText()));
        values.put(MediaStore.Audio.AudioColumns.COMPOSER, String.valueOf(songComposer.getText()));

        //Do the updates for the song
        if (mFileUri != null) {
            String selection = MediaStore.Audio.AudioColumns._ID + " = ?";
            String arg[] = {String.valueOf(currentSong.getID())};
            getContentResolver().update(URI, values, selection, arg);

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            getContentResolver().delete(ContentUris.withAppendedId(sArtworkUri, Long.parseLong(currentSong.getAlbumId())), null, null);
            ContentValues values4 = new ContentValues();
            values4.put("album_id", currentSong.getAlbumId());

            values4.put("_data", mFileUri.getPath());
            //getContentResolver().insert(sArtworkUri, values4);
            getContentResolver().insert(sArtworkUri, values4);
        }
        /*boolean success = false;
        if (newuri == null) {
            // Failed to insert in to the database. The most likely
            // cause of this is that the item already existed in the
            // database, and the most likely cause of that is that
            // the album was scanned before, but the user deleted the
            // album art from the sd card.
            // We can ignore that case here, since the media provider
            // will regenerate the album art for those entries when
            // it detects this.
            success = false;
        }
        else
        {
            success = true;
        }*/

        if (genreId != originalGenreId) {
            //Genre is a little trickier, we need to use the Genres.Members.Table and so use a different URI object  First, clean up the
            //old record
            if (originalGenreId != -1) {
                getContentResolver().delete(MediaStore.Audio.Genres.Members.getContentUri(
                        "external", originalGenreId), MediaStore.Audio.Genres.Members.AUDIO_ID + " = " + currentSong.getID(), null);
            }

            if (genreId != -1) {
                //Now enter the new record
                ContentValues values3 = new ContentValues();
                values3.put(MediaStore.Audio.Genres.Members.AUDIO_ID, currentSong.getID());
                getContentResolver().insert(MediaStore.Audio.Genres.Members.getContentUri("external", genreId), values3);
            }
        }
    }
}
