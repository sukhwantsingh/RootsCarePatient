package com.rootscare.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyImageCompress {

    private static final String TAG = "MyImageCompress";
    private static int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress

    public static File compressImageFilGottenFromCache(Activity activity, Uri resultUri, int quality){
        File fileGiven = new File(activity.getCacheDir(), resultUri.getLastPathSegment());
        String tempPath = "/sdcard/SihatkuTempImage_"+resultUri.getLastPathSegment();
        String returnedPath = "";
        File tempFile = new File(tempPath);
        if (!tempFile.exists()){
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = BitmapFactory.decodeFile (fileGiven.getPath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile, false);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.close();
            returnedPath = tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "compressImageFilGottenFromCache: "+tempPath);
        Log.d(TAG, "compressImageFilGottenFromCache_2: "+fileGiven.getAbsolutePath());
        Log.d(TAG, "compressImageFilGottenFromCache_3: "+tempFile.getAbsolutePath());
//        deleteFileFromGivenPath(returnedPath);
//        deleteFileFromGivenPath(tempFile);
        return tempFile;
    }

    public static Boolean deleteFileFromGivenPath(File tempFile){
        Boolean aBoolean = false;
//        File tempFile = new File(path);
        if (tempFile.exists()){
            aBoolean = tempFile.delete();
        }
        return aBoolean;
    }


}
