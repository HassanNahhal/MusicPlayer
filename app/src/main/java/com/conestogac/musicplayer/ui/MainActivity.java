package com.conestogac.musicplayer.ui;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.conestogac.musicplayer.R;

/**
 *  @author Changho Choi
 *  MainActivity is a launcher activity which will setup drawer, and sliding tab layout
 *  For each sliding layout, it will be connected with fragment
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private PagerSlidingTabStrip mSlidingTabLayout;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the Viewpager and set its pager PageAdapter so that it can display items
        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        mViewPager.setAdapter(new SongFragmentPagerAdapter(getApplicationContext(), getSupportFragmentManager()));

        // Give SlideingYabLayout the ViewPager, this must be done AFTER the ViewPager has had its PagerAdapter set
        mSlidingTabLayout = (PagerSlidingTabStrip) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // Get Drwaer pointer and settup  - listener, toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Get navigation view and setup listener for each item
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Process backkey at Drawer
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * For creating Search Actionbar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Process Search Actionbar menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Todo Handle Search Action Bar
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * For selecting each item at drawer, proper fragement will be called
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Todo Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.nav_album:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.nav_genre:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.nav_artist:
                mViewPager.setCurrentItem(3);
                break;
            case R.id.nav_playlist:
                mViewPager.setCurrentItem(4);
                break;
            case R.id.nav_setting:
                //todo setting for something
                break;
            case R.id.nav_about:
                //todo just show as dialog  who made this app
                break;
            default:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
