package com.base.module.pack.provider;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.base.module.pack.common.Utils;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
public class PackProvider extends ContentProvider {

    private static final String TAG="PackProvider";

    public static final String AUTHORITY = "com.base.module.pack";
    private static final String CONTENT_ICON = "icon";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse("content://" + AUTHORITY + ""), CONTENT_ICON);
    private static final String ICON_DIR = "packicon";
    private static final String ICON__SUFFIX = ".png";


    public static OutputStream getIconFileOutputStream(Context context, String iconName) throws FileNotFoundException {
        return new FileOutputStream(getIconPath(context, iconName));
    }

    private static String getIconPath(Context context, String iconName) {
        return context.getDir(ICON_DIR, Context.MODE_PRIVATE).toString() + "/" + iconName + ICON__SUFFIX;
    }

    public static void looserPermission(Context context,String iconName){
        Utils.chmod(getIconPath(context, iconName), "777");
    }

    public static void deleteIcon(Context context, String iconName) {
        Log.i(TAG, "iconName:"+iconName);
        File file=new File(getIconPath(context, iconName));
        if(file.exists()){
            Utils.deleteFile(file.getAbsolutePath());
        }


    }

    public static void deleteAllIcon(final Context context){

        for(File file:context.getDir(ICON_DIR, Context.MODE_PRIVATE).listFiles()){
            if(file.exists()){
                Utils.deleteFile(file.getAbsolutePath());
            }

        }


    }


    /*@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {

		SQLiteDatabase db=DateSource.getDateSource(getContext());

		return SQLiteContentHelper.getBlobColumnAsAssetFile(db, "", new String[]{});
	}*/



    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        String iconName = uri.getLastPathSegment();
        if (!Utils.isEmpty(iconName)) {
            //Log.i(TAG, "openFile:"+uri);
            return ParcelFileDescriptor.open(new File(getIconPath(getContext(), iconName)), ParcelFileDescriptor.MODE_READ_ONLY);
        }
        return null;
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri arg0, ContentValues arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3, String arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        // TODO Auto-generated method stub
        return 0;
    }
}