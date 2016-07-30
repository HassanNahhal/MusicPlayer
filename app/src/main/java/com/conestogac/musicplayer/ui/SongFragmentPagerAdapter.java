package com.conestogac.musicplayer.ui;

/**
 * Created by infomat on 16-07-29.
 */


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.conestogac.musicplayer.R;

import java.util.ArrayList;


/**
 *    @author: Francis Choi
 *    @use: Due to Actiobar.TAB is depreciated, Fragment pager
 */
public class SongFragmentPagerAdapter extends FragmentPagerAdapter {
    static ArrayList<PageView> PAGEVIEWS = new ArrayList<PageView>();
    private Context ctxt = null;

    //todo setup layout for each fragment
    static {
        PAGEVIEWS.add(new PageView(R.layout.fragment_library_view, R.string.tab_library));
        PAGEVIEWS.add(new PageView(R.layout.fragment_library_view, R.string.tab_album));
        PAGEVIEWS.add(new PageView(R.layout.fragment_library_view, R.string.tab_genre));
        PAGEVIEWS.add(new PageView(R.layout.fragment_library_view, R.string.tab_artist));
        PAGEVIEWS.add(new PageView(R.layout.fragment_library_view, R.string.tab_playlist));
    }

    public SongFragmentPagerAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt = ctxt;
    }


     /**
      * return count of pages
      * @return
      */

    @Override
    public int getCount() {
        return (PAGEVIEWS.size());
    }

     /**
      * Depend on position, return fragement
      * @param position
      * @return
      */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            //todo for Library View
            case 0:
                return LibraryViewFragment.newInstance(position);
            //todo for Album View
            case 1:
                return LibraryViewFragment.newInstance(position);
            //todo for Genre View
            case 2:
                return LibraryViewFragment.newInstance(position);
            //todo for Artist View
            case 3:
                return LibraryViewFragment.newInstance(position);
            //todo for PlayListview
            case 4:
                return LibraryViewFragment.newInstance(position);
            default:
                return null;
        }
    }

     /**
      * return title of page
      * @param position
      * @return
      */
    @Override
    public String getPageTitle(int position) {
        String ret_value;
        ret_value = (ctxt.getString(getView(position).titleId));
        return ret_value;
    }

     /**
      * return PAGEVIEW which has layout_id and title
      * @param position
      * @return
      */
    private PageView getView(int position) {
        return (PAGEVIEWS.get(position));
    }
}