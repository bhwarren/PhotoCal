package me.bowarren.photocal;

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered
 * preview of the Camera to the surface. We need to center the SurfaceView
 * because not all devices have cameras that support preview sizes at the same
 * aspect ratio as the device's display.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;

public class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final int ROTATE_TO_PORTRAIT = 90;
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    boolean mSurfaceCreated = false;

    Preview(Context context) {
        super(context);


        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            if (mSurfaceCreated) requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
        setCamera(camera);
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        boolean portrait = false;
        if(height>width){
            portrait = true;
        }


        if (mSupportedPreviewSizes != null) {
            //detect orientation
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                if(height>width && mPreviewSize.width > mPreviewSize.height) {
                    previewWidth = mPreviewSize.height;
                    previewHeight = mPreviewSize.width;
                }
                else{
                    previewWidth = mPreviewSize.width;
                    previewHeight = mPreviewSize.height;
                }

            }
            if(height>width && mCamera != null) {
                mCamera.setDisplayOrientation(90);
            }

            double scale = (double)previewHeight / height;
            //Log.e("f","aspect ration and height "+String.valueOf(scale)+" "+String.valueOf(height));


            int scaledChildWidth = (int) (previewWidth / scale);// (int) ((double) height / previewHeight * previewWidth);


            //Log.e("f", "adfasfdasdfasd " + String.valueOf(scaledChildWidth));
            int widthoffset = (width - scaledChildWidth) / 2;
            child.layout(widthoffset, 0, scaledChildWidth + widthoffset, height);

        }
    }


    public void surfaceCreated(SurfaceHolder holder) {

        //Log.e("f", "surface created");
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        if (mPreviewSize == null)
            requestLayout();

        mSurfaceCreated = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.e("f", "surface destroyed");

        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.3;
        double targetRatio = (double) w / h;
        if(h > w){
            targetRatio=(double)h / w;
        }

        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            //Log.e("f", "size w: "+String.valueOf(size.width)+" h: "+ toString().valueOf(size.height));
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff && size.height <= targetHeight) {
                Log.e("f", "found optimal size " + String.valueOf(size.width)+ " h= "+String.valueOf(size.height));

                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }


        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.e("f","can't get optimal size");

            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }


    private Size getOptimalPictureSize(Camera cam) {
        List<Size> sizes = cam.getParameters().getSupportedPreviewSizes();

        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        optimalSize = sizes.get(0);
        // find biggest size for saving
        for (Size size : sizes) {
            if(size.height*size.width > optimalSize.height*optimalSize.width){
                optimalSize = size;
            }
        }

        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if(mCamera == null)
            return;

        Size pic_size = getOptimalPictureSize(mCamera);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(pic_size.width, pic_size.height);

        if(h > w) {
            parameters.setRotation(ROTATE_TO_PORTRAIT);
            //parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);


        }
        Log.e("Optimal Size", mPreviewSize.width+" "+mPreviewSize.height);


        requestLayout();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    public Size getSize(){
        return mPreviewSize;
    }


}