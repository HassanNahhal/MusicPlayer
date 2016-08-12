package com.conestogac.musicplayer.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.conestogac.musicplayer.R;

import java.util.ArrayList;


/**
 *  Francis Choi
 *  Due to Actiobar.TAB is depreciated, Fragment pager
 */
public class SlideViewPagerAdapter extends FragmentPagerAdapter {
    static ArrayList<PageView> PAGEVIEWS = new ArrayList<>();
    private Context ctxt = null;
    private final static String TAG = "SlideViewPagerAdapter";

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
        switch (position) {
            //todo for Album View
            case 0:
                return AlbumViewFragment.newInstance(position);

            //todo for Library View
            case 1:
                return LibraryViewFragment.newInstance(position);

            //todo for Genre View
            case 2:
                return GenreViewFragment.newInstance(position);

            //todo for Artist View
            case 3:
                return ArtistViewFragment.newInstance(position);

            //todo for PlayListview
            case 4:
                return PlayListViewFragment.newInstance(position);

            //todo for Tagedit
            case 5:
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