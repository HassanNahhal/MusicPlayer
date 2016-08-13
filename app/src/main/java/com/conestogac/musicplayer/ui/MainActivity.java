package com.conestogac.musicplayer.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.conestogac.musicplayer.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 *  author Changho Choi
 *  MainActivity is a launcher activity which will setup drawer, and sliding tab layout
 *  For each sliding layout, it will be connected with fragment
 */
public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private PagerSlidingTabStrip mSlidingTabLayout;
    private ViewPager mViewPager;
    private int mTabPosition = 0;
    /**
     * Id to identity READ_STORAGE permission request.
     */
    private static final int REQUEST_WRITE_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Drwaer pointer and settup  - listener, toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Get navigation view and setup listener for each item
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupViewPager(savedInstanceState);
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
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Process Search Actionbar menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Todo Handle Search Action Bar
        return super.onOptionsItemSelected(item);

    }

    /**
     * For selecting each item at drawer, proper fragement will be called
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Todo Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id+1) {
            case R.id.nav_all:
                //todo this can be implemented as folder or just one or recently view
                mViewPager.setCurrentItem(SlideViewPagerAdapter.LIBRARY_VIEW);
                break;
            case R.id.nav_album:
                mViewPager.setCurrentItem(SlideViewPagerAdapter.ALBUM_VIEW);
                break;
            case R.id.nav_genre:
                mViewPager.setCurrentItem(SlideViewPagerAdapter.GENRE_VIEW);
                break;
            case R.id.nav_artist:
                mViewPager.setCurrentItem(SlideViewPagerAdapter.ARTIST_VIEW);
                break;
            case R.id.nav_playlist:
                mViewPager.setCurrentItem(SlideViewPagerAdapter.PLAYLIST_VIEW);
                break;
            case R.id.nav_setting:
                mViewPager.setCurrentItem(SlideViewPagerAdapter.TAG_EDITOR);
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

    @AfterPermissionGranted(REQUEST_WRITE_STORAGE)
    private void setupViewPager(Bundle savedInstanceState) {

        if (!EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_rationale_storage),
                    REQUEST_WRITE_STORAGE, WRITE_EXTERNAL_STORAGE);
            return;
        }



        // Get the Viewpager and set its pager PageAdapter so that it can display items
        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        mViewPager.setAdapter(new SlideViewPagerAdapter(getApplicationContext(), getSupportFragmentManager()));

        // Give SlideingYabLayout the ViewPager, this must be done AFTER the ViewPager has had its PagerAdapter set
        mSlidingTabLayout = (PagerSlidingTabStrip) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                mTabPosition = position;
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mTabPosition = (int) savedInstanceState
                    .getInt(KEY_LAYOUT_POSITION);
            Log.d(TAG, "Restored position: "+ mTabPosition);
            mViewPager.setCurrentItem(mTabPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putInt(KEY_LAYOUT_POSITION, mTabPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
      Easy permission is an open source to make easy to implement run time permission
      From SDK 23, user can remove permission after installation, and for some
      critical permissions, it is essential to get permission from user.
      To implement these, it is tedious job and looks code untidy.
      By using this open source, it looks more cleaner
      1. Add this module to Module Gradle
      2. Insert below code at the end of code
      3.
   */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(MainActivity.this, "Sorry, Application can not be started without permission", Toast.LENGTH_SHORT).show();
        finish();
    }

}
