package com.bastien.selflip;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bastien.selflip.views.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ShootActivity extends Activity {
    private Camera mCamera = null;

    private CameraPreview mPreview = null;

    private boolean frontCamera = false;

    private FrameLayout preview = null;

    private String imagePath = null;

    /**
     *  Activity-related methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot);

        //Front camera button
        ImageView flipCameraView = (ImageView) findViewById(R.id.camera_flip_button);
        if (Camera.getNumberOfCameras() > 1 ) {
            flipCameraView.setVisibility(View.VISIBLE);

            flipCameraView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frontCamera = !frontCamera;

                    if (frontCamera) {
                        setUpCamera(0);
                    } else {
                        setUpCamera(1);
                    }
                }
            });
        }

        preview = (FrameLayout) findViewById(R.id.camera_preview);

        Button captureButton = (Button) findViewById(R.id.capture_button);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCamera != null) {
                            mCamera.takePicture(null, null, mPicture);
                        }
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (frontCamera) {
            setUpCamera(0);
        } else {
            setUpCamera(1);
        }
    }

    /**
     *  Camera-related methods
     */

    private void setUpCamera(int cameraId) {
        releaseCamera();

        // Create an instance of Camera
        mCamera = getCameraInstance(cameraId);

        if (mCamera == null) {
            Toast toast = Toast.makeText(this, getString(R.string.no_camera), Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

        //Portrait mode
        mCamera.setDisplayOrientation(90);

        //Get optimal camera size for screen aspect ratio and min resolution
        Camera.Parameters cameraParameters = mCamera.getParameters();

        //Set continuous autofocus
        List<String> focusModes = cameraParameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        try {
            mCamera.setParameters(cameraParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mPreview != null) {
            preview.removeView(mPreview);
        }

        //Get size information on preview
        int previewWidth = Math.min(cameraParameters.getPreviewSize().height, cameraParameters.getPreviewSize().width);
        int previewHeight = Math.max(cameraParameters.getPreviewSize().height, cameraParameters.getPreviewSize().width);
        float previewRatio = ((float) previewWidth)/previewHeight;

        //Get size information on window
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int screenWidth = size.x;
        int screenHeight = size.y;
        float screenRatio = ((float) screenWidth)/screenHeight;

        //Create Preview view
        mPreview = new CameraPreview(this, mCamera);
        ViewGroup.LayoutParams params = null;

        //Set preview size so it doesn't strech (equivalent of a center crop)
        if (previewRatio > screenRatio) {
            params = new ViewGroup.LayoutParams((int) (screenHeight * previewRatio), screenHeight);
            mPreview.setX(-(params.width - screenWidth)/2);
        } else {
            params = new ViewGroup.LayoutParams(screenWidth, (int) (screenWidth / previewRatio));
            mPreview.setY(-(params.height - screenHeight)/2);
        }

        mPreview.setLayoutParams(params);

        //Set preview as the content of activity.
        preview.addView(mPreview);
    }

    public Camera getCameraInstance(int cameraId) {
        Camera c = null;

        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            Log.d("BAB", "Camera not available");
            Toast toast = Toast.makeText(this, getString(R.string.no_available_camera), Toast.LENGTH_LONG);
            toast.show();
        }

        return c;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();

            if (pictureFile == null){
                //Check storage permission
                pictureFailed();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                //File not found
                pictureFailed();
            } catch (IOException e) {
                //Error accessing file
                pictureFailed();
            }

            imagePath = pictureFile.getAbsolutePath().toString();

//            if (formattedPicture.getHeight() < formattedPicture.getWidth()) {
//                if (frontCamera) {
//                    imageCamera = 0;
//                    formattedPicture = ImageUtils.rotateImage(formattedPicture);
//                } else {
//                    imageCamera = 1;
//                    formattedPicture = ImageUtils.reverseRotateImage(formattedPicture);
//                    formattedPicture = ImageUtils.mirrorBitmap(formattedPicture);
//                }
//            }
        }
    };

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private void pictureFailed() {
        Toast toast = Toast.makeText(this, getString(R.string.picture_failed), Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     *  Image-related methods
     */

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if (isSDPresent()){
            pictureFailed();
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Selflip");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                //Failed to create directory
                pictureFailed();
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "SELFLIP_IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    private boolean isSDPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

}
