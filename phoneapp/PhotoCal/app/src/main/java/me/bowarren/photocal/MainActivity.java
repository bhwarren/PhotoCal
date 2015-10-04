package me.bowarren.photocal;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
      //  implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new CameraFragment())
                .commit();
//
//        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        switch(position){
//            // camera preview
//            case 0:
////                fragmentManager.beginTransaction()
////                        .replace(R.id.container, new CameraFragment())
////                        .commit();
//                break;
//            //upload picture
//            case 1:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                        .commit();
//                break;
//
//            //open calendar
//            case 2:
//                long startMillis = System.currentTimeMillis();
//                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
//                builder.appendPath("time");
//                ContentUris.appendId(builder, startMillis);
//                Intent intent = new Intent(Intent.ACTION_VIEW)
//                        .setData(builder.build());
//                startActivity(intent);
//                break;
//
//            //show events locally taken
//            case 3:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, new EventsFragment())
//                        .commit();
//                break;
//
//            //about
//            case 4:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, new AboutFragment())
//                        .commit();
//                break;
//        }
//    }

//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 0:
//                mTitle = getString(R.string.title_section0);
//                break;
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
//            case 4:
//                mTitle = getString(R.string.title_section4);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.drawer, menu);
//            restoreActionBar();
//            return true;
//        }
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //here is where you add functionality for the settings button
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(id){
            // camera preview
            case R.id.snap_photo:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new CameraFragment())
                        .commit();
                break;
            //upload picture
            case R.id.upload_picture:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(1))
                        .commit();
                break;

            //open calendar
            case R.id.open_calendar:
                long startMillis = System.currentTimeMillis();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, startMillis);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
                break;

            //show events locally taken
            case R.id.show_events:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new EventsFragment())
                        .commit();
                break;

            //about
            case R.id.about:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AboutFragment())
                        .commit();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


}
