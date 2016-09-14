/****************************************************************************
 *
 * FILENAME:        com.base.module.db.Date.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-2-2
 *
 * DESCRIPTION:     The class encapsulates the music ring tone operations.
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2011 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.base.module.pack.db;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.base.module.pack.R;
public class DateSource {
    protected static final String TAG = "DateSource";
    private final static String DATABASE_PATH = "/data/data/com.base.module.pack/databases";
    private final static String DATABASE_FILENAME = "pack.db";
    private static String PREFS_NAME = "Package_Manager_Option";
    private static SQLiteDatabase mDatabase = null;
    private static String mDatabaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;

    public static synchronized SQLiteDatabase getDateSource(Context mContext) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
            File dir = new File(DATABASE_PATH);
            if (!dir.exists())
                dir.mkdirs();
            File f = null;
            /**
             * while it 's the first insert
             */
            if (settings.getBoolean("pack_magager_First", true)) {
                f = new File(mDatabaseFilename);
                if (f.exists()) {
                    f.delete();
                }
                settings.edit().putBoolean("pack_magager_First", false).commit();				
            }
            /**
             * get a new db from the dir while there never have a db before
             **/
            mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabaseFilename, null);

            return mDatabase;
        } catch (Exception e) {
            Log.e(TAG, "error open datebase---" + e.toString());
            Toast.makeText(mContext, mContext.getString(R.string.low_space_promt), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    /**
     * uncompress the raw
     * 
     * @param mCtx
     * @param mDBRawResource
     * @return
     */
    public static synchronized int ZipDateSource(Context mCtx, int mDBRawResource) {
        int len = 1024;
        int readCount = 0, readSum = 0;
        byte[] buffer = new byte[len];
        @SuppressWarnings("unused")
        int StreamLen = 0;
        InputStream inputStream;
        OutputStream output;
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.i("Package_Manager-DataSource", "needing SD!!!");
            }
            inputStream = mCtx.getResources().openRawResource(mDBRawResource);
            output = new FileOutputStream(mDatabaseFilename);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            BufferedInputStream b = new BufferedInputStream(zipInputStream);
            StreamLen = (int) zipEntry.getSize();
            while ((readCount = b.read(buffer)) != -1) {
                // readCount = b.read(buffer);
                output.write(buffer, 0, readCount);
                readSum = readSum + readCount;
            }
            output.flush();
            output.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("zip io", e.toString());
        }
        return readSum;
    }

    /**
     * uncompress the file when it had compressed
     * @param mCtx
     * @param mDBRawResource
     * @return
     */
    public static int nDateSource(Context mCtx, int mDBRawResource) {
        int count = 0;
        try {
            InputStream is = mCtx.getResources().openRawResource(mDBRawResource);
            FileOutputStream fos = new FileOutputStream(mDatabaseFilename);
            byte[] buffer = new byte[8192];
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            Log.e("nDateSource io exception", e.toString());
        }
        return count;
    }

    public static void close() {
        mDatabase.close();
    }
}