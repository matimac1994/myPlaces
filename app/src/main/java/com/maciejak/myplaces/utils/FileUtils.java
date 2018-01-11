package com.maciejak.myplaces.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mati on 17.12.2017.
 */

public class FileUtils {

    public static java.io.File savePhotoToFile(Uri selectedImg, Context context) {
        Bitmap bitmapPhoto = null;
        java.io.OutputStream os;
        File file = null;
        try {
            file = createImageFile(context);
            bitmapPhoto = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImg);
            os = new FileOutputStream(file);
            bitmapPhoto.compress(Bitmap.CompressFormat.JPEG, 75, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static Uri createUriForTakePhoto(Context context){
        java.io.File photoFile = null;
        Uri photoURI = null;
        try {
            photoFile = createImageFile(context);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(context,
                    "com.maciejak.myplaces.fileprovider",
                    photoFile);
        }
        return photoURI;
    }

    private static java.io.File createImageFile(Context context) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        java.io.File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        java.io.File image = java.io.File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static boolean removeFileFromDevice(Uri uri) {
        File file = new File(uri.toString());
        return file.delete();
    }

    public static boolean removeFileFromDevice(String uri) {
        File file = new File(uri);
        return file.delete();
    }

}
