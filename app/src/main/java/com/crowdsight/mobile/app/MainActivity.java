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







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

//        Now, hide the fragments initially in the onCreate() method:

        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
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

        Toast.makeText(this, position + "was selected", Toast.LENGTH_LONG).show();
        drawerListView.setItemChecked(position, true);
        setTitle(navigationMenu[position]);
    }

    //NavigationDrawer Ends Here


//    showing a given fragment and hiding all other fragments

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            }
//            else {
//                transaction.hide(fragments[i]);
//            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    //    flag to indicate visible activity
    private boolean isResumed = false;


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
                showFragment(SPLASH, false);

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


//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // only add the menu when the selection fragment is showing
//        if (fragments[SELECTION].isVisible()) {
//            if (menu.size() == 0) {
//                settings = menu.add(R.string.settings);
//            }
//            return true;
//        } else {
//            menu.clear();
//            settings = null;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.equals(settings)) {
//            showFragment(SETTINGS, true);
//            return true;
//        }
//        return false;
//    }


//  ****************************************************************************************
//
//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
////        update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//
//        switch (position) {
//            case 0:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, Venues.newInstance("param1", "param2"))
//                        .commit();
//                break;
////            case 1:
////                fragmentManager.beginTransaction()
////                        .replace(R.id.container, inside.PlaceholderFragment.newInstance(position + 1))
////                        .commit();
////                break;
////            case 2:
////                fragmentManager.beginTransaction()
////                        .replace(R.id.container, inside.PlaceholderFragment.newInstance(position + 1))
////                        .commit();
////                break;
////            case 3:
////                fragmentManager.beginTransaction()
////                        .replace(R.id.container, inside.PlaceholderFragment.newInstance(position + 1))
////                        .commit();
////                break;
//
//        }
//
//    }
//
//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }
//
//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
////            case 2:
////                mTitle = getString(R.string.title_section2);
////                break;
//
//
//        }
//
//    }
//
//
//
//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            Intent i = new Intent(MainActivity.this, TrackLocation.class);
//            startActivity(i);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//       onNavigationDrawerItemSelected(1);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()){
//                case R.id.imageOffers:
//                    Toast.makeText(v.getContext(), "Offer not available!", Toast.LENGTH_SHORT);
//                    break;
//                case R.id.imageRewards:
//                    Toast.makeText(v.getContext(), "Rewards not available!", Toast.LENGTH_SHORT);
//                    break;
//            }
//        }
//    }


}



////  The code below is old prototype code with menu
//
////        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
////        Venues.OnFragmentInteractionListener {
////
//    /**
//     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
//     */
//    private NavigationDrawerFragment mNavigationDrawerFragment;
//
//    /**
//     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
//     */
//    private CharSequence mTitle;
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////
////        mNavigationDrawerFragment = (NavigationDrawerFragment)
////                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
////        mTitle = getTitle();
////
////        // Set up the drawer.
////        mNavigationDrawerFragment.setUp(
////                R.id.navigation_drawer,
////                (DrawerLayout) findViewById(R.id.drawer_layout));
////    }
//
//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }
//
//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//
//        switch (position) {
//            case 0:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, Venues.newInstance("param1", "param2"))
//                        .commit();
//                break;
//            case 1:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                        .commit();
//                break;
//            case 2:
//                Intent i = new Intent(MainActivity.this, TrackLocation.class);
//                startActivity(i);
//                break;
//            case 3:
//            try {
//
//                    Intent t = new Intent(MainActivity.this, TrackLocation.class);
//                    startActivity(t);
//                    break;
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                Intent i = new Intent(MainActivity.this, TrackLocation.class);
//                startActivity(i);
//                break;
//            case 4:
//                Intent t = new Intent(MainActivity.this, TrackLocation.class);
//                startActivity(t);
//                break;
//        }
//    }
//
//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(i);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//       onNavigationDrawerItemSelected(3);
//    }
//
//
//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()){
//                case R.id.imageOffers:
//                    Toast.makeText(v.getContext(), "Offer not available!", Toast.LENGTH_SHORT);
//                    break;
//                case R.id.imageRewards:
//                    Toast.makeText(v.getContext(), "Rewards not available!", Toast.LENGTH_SHORT);
//                    break;
//            }
//        }
//    }

//}
