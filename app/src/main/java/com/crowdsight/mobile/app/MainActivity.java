package com.crowdsight.mobile.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;


public class MainActivity extends ActionBarActivity {

    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    // private static final int SETTINGS = 2;


    private static final int FRAGMENT_COUNT = SELECTION + 1;


    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    // private MenuItem settings;




    private DrawerLayout drawer_Layout;
    private ListView drawerListView;
    private String[] navigationMenu;
    private CharSequence mTitle;
    private ActionBarDrawerToggle drawerListener;
    private CharSequence mDrawerTitle;
    Session session = Session.getActiveSession();
    private boolean menuChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

//        Now, hide the fragments initially in the onCreate() method:

        FragmentManager fm = getSupportFragmentManager();
        SplashFragment loginFragment = (SplashFragment) fm.findFragmentById(R.id.splashFragment);
        SelectionFragment profileFragment = (SelectionFragment) fm.findFragmentById(R.id.selectionFragment);




        fragments[SPLASH] = loginFragment;
        fragments[SELECTION] = profileFragment;

        // fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        // Navigation Drawer starts here


        drawer_Layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.drawerList);
        navigationMenu = getResources().getStringArray(R.array.home_menu);


        drawerListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navigationMenu));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        drawerListener = new ActionBarDrawerToggle(
                this,
                drawer_Layout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer_Layout.setDrawerListener(drawerListener);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
            // Toast.makeText(this, navigationMenu[position]+"was selected", Toast.LENGTH_LONG).show();
        }
    }

    private void selectItem(int position) {

        TrackLocationFragment tFragment = new TrackLocationFragment();
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();



        switch (position) {
            case 0:
                Intent i = new Intent(this, TrackLocation.class);
                startActivity(i);
                break;
            case 1:

                transaction.replace(R.id.mainContent, tFragment);
                transaction.commit();




                break;
            case 2:


                showFragment(SPLASH, false);
                break;


        }

        setTitle(navigationMenu[position]);
        drawer_Layout.closeDrawer(drawerListView);
    }

    //NavigationDrawer Ends Here

    //    flag to indicate visible activity
    private boolean isResumed = false;


//    showing a given fragment and hiding all other fragments

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            }
            else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }


    //    setting the flag when the activity is paused or resumed
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

//
//    define a private method that will be called due to session state changes.
//    The method shows the relevant fragment based on the person's authenticated state.
//    Only handle UI changes when the activity is visible by making use of the isResumed flag.
//    The fragment back stack is first cleared before the showFragment() method is called with the appropriate fragment info:


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment
                showFragment(SELECTION, false);




            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                showFragment(SPLASH, false);


            }
        }
    }


//    make sure you handle the case where fragments are newly instantiated and the authenticated
//    versus nonauthenticated UI needs to be properly set.
//    Override the onResumeFragments() method to handle the session changes:

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            showFragment(SELECTION, false);

            // showFragment(SPLASH, false);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showFragment(SPLASH, false);

        }
    }

//    use the UiLifecycleHelper to track the session and trigger a session state change listener.
//    First, create a private variable for the UiLifecycleHelper object and the session state change listener implementation:

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };

}




