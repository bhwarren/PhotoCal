package me.bowarren.photocal;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *app
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
        import android.content.Context;
        import android.content.Intent;
        import android.hardware.Camera;
        import android.hardware.Camera.CameraInfo;
        import android.hardware.Camera.Size;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;
        import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class CameraFragment extends android.support.v4.app.Fragment {

    public Preview mPreview;
    Camera mCamera;
    int mNumberOfCameras;
    int mCurrentCamera;  // Camera ID currently chosen
    int mCameraCurrentlyLocked;  // Camera ID that's actually acquired

    // The first rear facing camera
    int mDefaultCameraId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a container that will hold a SurfaceView for camera previews
        mPreview = new Preview(this.getActivity());
        mPreview.setOnTouchListener( new OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        if(event.getAction() != MotionEvent.ACTION_DOWN){
                            //Log.e("F","FfFFFFFFFFFFFFFF");
                            return false;
                        }

                        mCamera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                //make directory and save the picture
                                File directory = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/PhotoCal");

                                if (!directory.exists()) {
                                    directory.mkdirs();
                                }

                                File pic = new File(directory, "test.jpg");
                                if (pic.exists()) {
                                    pic.delete();
                                }

                                //try writing the picture
                                try {
                                    FileOutputStream out = new FileOutputStream(pic);
                                    out.write(data);
                                    out.close();

                                    //here's where we upload the picture & add its info the the native calendar
                                    CalendarHelper.uploadAndAddToCal(pic, getActivity());

                                }
                                catch(FileNotFoundException e){
                                    Toast.makeText(getContext(), "failed to find file", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                                catch(IOException e){
                                    Toast.makeText(getContext(), "Io exception", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        });

                        return true;
                    };
                }
        );

        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the rear-facing ("default") camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCurrentCamera = mDefaultCameraId = i;
            }
        }
        setHasOptionsMenu(mNumberOfCameras > 1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Add an up arrow to the "home" button, indicating that the button will go "up"
        // one activity in the app's Activity heirarchy.
        // Calls to getActionBar() aren't guaranteed to return the ActionBar when called
        // from within the Fragment's onCreate method, because the Window's decor hasn't been
        // initialized yet.  Either call for the ActionBar reference in Activity.onCreate()
        // (after the setContentView(...) call), or in the Fragment's onActivityCreated method.
        Activity activity = this.getActivity();
        android.app.ActionBar actionBar = activity.getActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mPreview;
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e("f", "resumed");
        // Use mCurrentCamera to select the camera desired to safely restore
        // the fragment after the camera has been changed
        //Log.e("F", "resumed, getting camera back");
        mCamera = Camera.open(mCurrentCamera);
        mCameraCurrentlyLocked = mCurrentCamera;
        mPreview.setCamera(mCamera);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e("f", "paused");

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            //Log.e("F", "releasing camera");
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mNumberOfCameras > 1) {
            // Inflate our menu which can gather user input for switching camera
            inflater.inflate(R.menu.camera_menu, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.menu_switch_cam:
//                // Release this camera -> mCameraCurrentlyLocked
//                if (mCamera != null) {
//                    mCamera.stopPreview();
//                    mPreview.setCamera(null);
//                    mCamera.release();
//                    mCamera = null;
//                }
//
//                // Acquire the next camera and request Preview to reconfigure
//                // parameters.
//                mCurrentCamera = (mCameraCurrentlyLocked + 1) % mNumberOfCameras;
//                mCamera = Camera.open(mCurrentCamera);
//                mCameraCurrentlyLocked = mCurrentCamera;
//                mPreview.switchCamera(mCamera);
//
//                // Start the preview
//                mCamera.startPreview();
//                return true;
//
//            case android.R.id.home:
//                Intent intent = new Intent(this.getActivity(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}