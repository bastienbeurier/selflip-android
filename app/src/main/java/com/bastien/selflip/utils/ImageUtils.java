package com.bastien.selflip.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bastien on 6/15/14.
 */
public class ImageUtils {

    static public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Selflip");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Failed to create directory
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "SELFLIP_IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    static public Bitmap rotateImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    static public Bitmap reverseRotateImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    static public Bitmap mirrorBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    static public Bitmap decodeFile(String filePath) {
        Bitmap bitmap = null;

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        try {
            FileInputStream fis = new FileInputStream(filePath);
            BitmapFactory.decodeStream(fis, null, o);

            fis.close();

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            fis = new FileInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static public Bitmap resize(Bitmap bitmap, float previewRatio) {
        float pictureRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();

        if (pictureRatio > previewRatio) {
            int currentHeight = bitmap.getHeight();
            int targetHeight = (int) (bitmap.getWidth() * previewRatio);

            return Bitmap.createBitmap(
                    bitmap,
                    0,
                    (currentHeight - targetHeight) / 2,
                    bitmap.getWidth(),
                    targetHeight);
        } else {

            int currentWidth = bitmap.getWidth();
            int targetWidth = (int) (bitmap.getHeight() / previewRatio);

            return Bitmap.createBitmap(
                    bitmap,
                    (currentWidth - targetWidth) / 2,
                    0,
                    targetWidth,
                    bitmap.getHeight());
        }
    }

    static public Bitmap cropToTopHalf(Bitmap bitmap) {
        return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight()/2);
    }

}
