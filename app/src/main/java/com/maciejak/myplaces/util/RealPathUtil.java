package com.maciejak.myplaces.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.io.File;

/**
 * Created by Mati on 04.11.2017.
 */

public class RealPathUtil {

    public static File getFileFromURI(Context context, Uri contentUri) {
        String path = contentUri.getPath();
        File file = new File(path);

        if (file.canRead())
            return file;
        else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                file = new File(path);
                return file;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

}
