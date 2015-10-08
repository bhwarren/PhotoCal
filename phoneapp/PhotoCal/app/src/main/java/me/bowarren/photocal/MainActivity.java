package me.bowarren.photocal;

import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

      //  implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean finishedLoading = false;
    private CharSequence mTitle;
    private CameraFragment previewFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        previewFragment = new CameraFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, previewFragment, "Camera_Fragment")
                .commit();


        //possibly not needed
        //CalendarHelper.getRealInfo(232, this);
        finishedLoading = true;
//
//        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
    }



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
        CameraFragment cameraFrag = (CameraFragment)fragmentManager.findFragmentByTag("Camera_Fragment");
        if (! cameraFrag.isVisible()) {
            // add your code here
            fragmentManager.popBackStack();
        }
        switch(id){
            // camera preview
//            case R.id.snap_photo:
////                fragmentManager.beginTransaction()
////                        .replace(R.id.container, new CameraFragment(), "Camera_Fragment")
////                        .commit();
//                break;
            //upload picture
            case R.id.upload_picture:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, PlaceholderFragment.newInstance(1))
//                        .addToBackStack(null)
//                        .commit();
                selectPhotoThenUpload();
                break;

            //open calendar
            case R.id.open_calendar:
                CalendarHelper.openCalendar(this);
                break;

            //show events locally taken
            case R.id.show_events:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new EventsFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            //about
            case R.id.about:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AboutFragment())
                        .addToBackStack(null)
                        .commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("f", "got activity result: " + String.valueOf(requestCode)+"   result code: "+String.valueOf(resultCode));


        //finished letting user edit the event
        //however, calendar doesn't return an intent, so work around, getting the event_id
        //and set the calendar_id in the PhotoCalEvent

        //save & upload the picture data
        if(requestCode == 1 && resultCode == RESULT_OK){
            Log.e("e", "got selected picture from user");

            if(data != null){
                Uri picUri = data.getData();
                File selectedFile = new File(picUri.getPath());
                Log.e("f", selectedFile.toString());

                PhotoCalEvent event = CalendarHelper.getEventInfo(selectedFile, this);
                CalendarHelper.addToCalendar(event, this);


//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//                intent.setDataAndType(picUri, "image/jpg");
//                startActivity(intent);

            }
            return;
        }
        else if(requestCode == 1 && resultCode != RESULT_OK){
            Log.e("f", "pressed back button @ wrong time");
            //unlock camera not working
//            previewFragment.mCamera.stopPreview();
//            previewFragment.mCamera.startPreview();
            //previewFragment.getView().invalidate();

        }

        if(requestCode == 0) {
            Log.e("f", "adding the real event to the eventHolder w/ id: " + String.valueOf(CalendarHelper.lastEventIdAdded));


            final Activity tempAct = this;

            //        // Do something after 5s = 5000ms
            //        PhotoCalEvent finalEvent = CalendarHelper.getRealInfo(
            //                CalendarHelper.lastEventIdAdded, tempAct);
            //        //CalendarHelper.addToCalendar(finalEvent, this);
            //
            //        if(finalEvent != null) {
            //            EventHolder eh = new EventHolder(getApplicationContext());
            //            eh.addEvent(finalEvent);
            //        }
            //        else{
            //            //try again
            //            Toast.makeText(tempAct.getApplicationContext(), "Failed to get Info from native calendar, sticking w/ defaults", Toast.LENGTH_LONG).show();
            //
            //        }



            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    PhotoCalEvent finalEvent = CalendarHelper.getRealInfo(
                            CalendarHelper.lastEventIdAdded, tempAct);
                    //CalendarHelper.addToCalendar(finalEvent, this);

                    if (finalEvent != null) {
                        EventHolder eh = new EventHolder(getApplicationContext());
                        eh.addEvent(finalEvent);
                    } else {
                        //try again
                        Toast.makeText(tempAct.getApplicationContext(), "Failed to get Info from native calendar, sticking w/ defaults", Toast.LENGTH_LONG).show();

                    }
                }
            }, 1000);

        }



    }

    private void selectPhotoThenUpload(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }



}
