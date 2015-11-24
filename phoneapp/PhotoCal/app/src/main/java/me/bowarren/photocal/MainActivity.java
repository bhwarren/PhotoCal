package me.bowarren.photocal;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static int fragmentId = R.id.preview;
    private MenuItem previewButton;
    private FloatingActionButton fab;

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

        fab = (FloatingActionButton) this.findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take picture
                Toast.makeText(getApplicationContext(), "Saving Picture", Toast.LENGTH_SHORT).show();
                previewFragment.takePicture();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, previewFragment, "Camera_Fragment")
                .commit();


        switch(fragmentId) {
            //events
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

        //possibly not needed
        //CalendarHelper.getRealInfo(232, this);
        finishedLoading = true;

//        if(fragmentId == R.id.preview)
//            switchIconsToPreview(true);
//        else
//            switchIconsToPreview(false);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        previewButton = menu.findItem(R.id.action_preview);
        if(fragmentId == R.id.preview)
            previewButton.setVisible(false);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        //here is where you add functionality for the settings button
        FragmentManager fragmentManager = getSupportFragmentManager();

        //first thing to do if there is a fragment loaded is to unload it to show preview always
        //showPreview(null);
        fragmentManager.popBackStack();
        switchIconsToPreview(false);


        fragmentId = item.getItemId();

        //then add the selected fragment
        switch(fragmentId){
            //upload picture
            case R.id.upload_picture:
                selectPhotoThenUpload();
                break;

            //open calendar
            case R.id.open_calendar:
                switchIconsToPreview(true); //when coming back, always goes to preview for some reason
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
        Log.e("f", "got activity result: " + String.valueOf(requestCode) + "   result code: " + String.valueOf(resultCode));


        //finished letting user edit the event
        //however, calendar doesn't return an intent, so work around, getting the event_id
        //and set the calendar_id in the PhotoCalEvent

        //save & upload the picture data
        if(requestCode == 1 && resultCode == RESULT_OK){
            Log.e("e", "got selected picture from user");


            if(data != null){
                Uri picUri = data.getData();
                File selectedFile;
                File directory;
                File dest;
                File prev;


                try{
                    InputStream selectedInput = getContentResolver().openInputStream(picUri);
                    selectedFile = new File(picUri.getPath());

                     directory = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/PhotoCal");
                     dest = new File(directory.getAbsolutePath() + "/"+selectedFile.getName());
                     prev = new File(dest.getPath()+"_preview.jpg");

                    FileOutputStream destOut = new FileOutputStream(dest);
                    IOUtils.copy(selectedInput, destOut);


                    //FileUtils.copyFile(selectedFile, dest);

                    FileOutputStream out = new FileOutputStream(prev);
                    Bitmap preview = previewFragment.scaleImg(BitmapFactory.decodeFile(dest.getAbsolutePath()), 250);
                    preview.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    Log.e("f", selectedFile.toString());

                    //CalendarHelper.uploadAndAddToCal(selectedFile, this);
                    PhotoCalEvent event = new PhotoCalEvent("Not Set", null, null, "?", "?", dest, this);
                    CalendarHelper.addToList(event, this);

                    showEvents();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }




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

        //adding the event after recieved
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
                        Log.e("final event not null", finalEvent.toString());
                        EventHolder eh = new EventHolder(getApplicationContext());
                        File pic = eh.getPic(CalendarHelper.lastIndexSelected);
                        finalEvent.image = pic;
                        eh.addEvent(finalEvent);
                        eh.removeEvent(CalendarHelper.lastIndexSelected);
                        //eh.savedEvents.clear();

                        showPreview(null);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new EventsFragment())
                                .addToBackStack(null)
                                .commit();
                        switchIconsToPreview(false);

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


    //has to have the MenuItem there, otherwise a crash b/c it's onclick is defined in xml
    public void showPreview(MenuItem item){
        FragmentManager fragmentManager = getSupportFragmentManager();
        CameraFragment cameraFrag = (CameraFragment)fragmentManager.findFragmentByTag("Camera_Fragment");

        if (! cameraFrag.isVisible()) {
            fragmentManager.popBackStack();
        }
        fragmentId = R.id.preview;
        switchIconsToPreview(true);

    }

    public void showEvents(){
        //showPreview(null);
        getSupportFragmentManager().popBackStack();
        //switchIconsToPreview(false); //needed to remove icons for events fragment

        fragmentId = R.id.show_events;
        switchIconsToPreview(false);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new EventsFragment())
                .addToBackStack(null)
                .commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 &&
                fragmentId != R.id.preview) {

            showPreview(null);
            switchIconsToPreview(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void switchIconsToPreview(boolean truth){
        //preview icons
        if(truth){
            if(previewButton != null)
                previewButton.setVisible(false);
            if(fab != null)
                fab.setVisibility(View.VISIBLE);
        }


        //everything else icons
        else{
            if(previewButton != null)
                previewButton.setVisible(true);
            if(fab != null)
                fab.setVisibility(View.INVISIBLE);


        }
    }

}
