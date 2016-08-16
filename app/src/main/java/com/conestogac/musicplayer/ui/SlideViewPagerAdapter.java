package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.conestogac.musicplayer.R;

import java.util.ArrayList;


/**
 *  Changho Choi
 *  Due to Actiobar.TAB is depreciated, pagerslidingtabstrip is used.
 *  This is open source which is created based on Google's FragmentPageView
 */
public class SlideViewPagerAdapter extends FragmentPagerAdapter {
    static ArrayList<PageView> PAGEVIEWS = new ArrayList<>();
    private Context ctxt = null;
    private final static String TAG = "SlideViewPagerAdapter";

    //definition of tab order
    public final static int ALBUM_VIEW = 1;
    public final static int LIBRARY_VIEW = 2;
    public final static int GENRE_VIEW = 3;
    public final static int ARTIST_VIEW = 4;
    public final static int PLAYLIST_VIEW = 5;
    public final static int TAG_EDITOR = 6;


    //todo setup layout for each fragment
    static {
        PAGEVIEWS.add(new PageView(R.layout.fragment_recycler_view, R.string.tab_album));
        PAGEVIEWS.add(new PageView(R.layout.fragment_list_view, R.string.tab_library));
        PAGEVIEWS.add(new PageView(R.layout.fragment_recycler_view, R.string.tab_genre));
        PAGEVIEWS.add(new PageView(R.layout.fragment_recycler_view, R.string.tab_artist));
        PAGEVIEWS.add(new PageView(R.layout.fragment_recycler_view, R.string.tab_playlist));
        PAGEVIEWS.add(new PageView(R.layout.fragment_list_view, R.string.tab_tag_edit));
    }

    public SlideViewPagerAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt = ctxt;
    }


     /**
      * return count of pages
      */

    @Override
    public int getCount() {
        return (PAGEVIEWS.size());
    }

     /**
      * Depend on position, return fragement
      */
    @Override
    public Fragment getItem(int position) {
        switch (position+1) {
            //for Album View
            case ALBUM_VIEW:
                return AlbumViewFragment.newInstance(position);

            //for Library View
            case LIBRARY_VIEW:
                return LibraryViewFragment.newInstance(position);

            //for Genre View
            case GENRE_VIEW:
                return GenreViewFragment.newInstance(position);

            //for Artist View
            case ARTIST_VIEW:
                return ArtistViewFragment.newInstance(position);

            //for PlayListview
            case PLAYLIST_VIEW:
                return PlayListViewFragment.newInstance(position);

            //for Tagedit
            case TAG_EDITOR:
                return LibraryViewFragment.newInstance(position);

            default:
                Log.d(TAG, "OOPS!! Wrong index for tab");
                return null;
        }
    }

     /**
      * return title of page
      */
    @Override
    public String getPageTitle(int position) {
        String ret_value;
        ret_value = (ctxt.getString(getView(position).titleId));
        return ret_value;
    }

     /**
      * return PAGEVIEW which has layout_id and title
      */
    private PageView getView(int position) {
        return (PAGEVIEWS.get(position));
    }
}