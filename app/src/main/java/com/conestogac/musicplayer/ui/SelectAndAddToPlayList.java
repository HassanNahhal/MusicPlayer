package com.conestogac.musicplayer.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Playlist;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.DBHelper;

import java.util.ArrayList;

/**
 * For Playlist Selection and Adding to DB inherited from ListActivity
 * Author: Changho Choi
 */
public class SelectAndAddToPlayList extends ListActivity {
    private final static String TAG="SelectAndAddToPlayList";
    private ArrayList<Playlist> playLists = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<Song> songList;
    public static final String EXTRA_SONGLIST = "songlist";

    private DBHelper dbHelper  = new DBHelper(SelectAndAddToPlayList.this);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_and_add_to_play_list);
        final ListView listview = (ListView) findViewById(R.id.listView);
        this.setTitle(getString(R.string.select_playlist));

        //get songlist from intent and sort
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_SONGLIST)) {
            songList = (ArrayList<Song>) getIntent().getExtras().get(EXTRA_SONGLIST);
        }

        //read playlist from db
        readDataFromDB();

    }

    /**
     *  Define OnClick on List Item
     * @param l listview
     * @param v View
     * @param position: position of selection
     * @param id
     */

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Insert Songs at selected Playlist
        dbHelper.insertPlaylistSong(playLists.get(position).getID(),songList);
        Toast.makeText(this, songList.size()+" song(s) are added to playlist", Toast.LENGTH_SHORT).show();
        finish();
    }


    /**
     * Database query can be a time consuming task ..
     * so its safe to call database query in another thread
     */
    private void readDataFromDB() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                playLists = dbHelper.getAllPlaylistsAsArrayList();
                getNameListFromPlaylist();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, names);
                setListAdapter(adapter);
            }
        });
    }

    /**
     * To display playlist name, extracting name from playlist
     */
    private void getNameListFromPlaylist() {
        for (Playlist playlist: playLists) {
            names.add(playlist.getName());
        }
    }
}
