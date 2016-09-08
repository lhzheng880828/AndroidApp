package com.base.module.pack.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.R.integer;
import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
/**
 * Copyright:GrandStream
 * @Description:
 */

public class DateTimeUtils {
    private final static String TAG = DateTimeUtils.class.getSimpleName();

    public static String timeShow(Context context, long timeMillis,boolean splite) {
    	//DateFormat.format(inFormat, inTimeInMillis);
        String timeStr = (String) DateFormat.format("YYYY-MM-dd-HH-mm-ss", timeMillis);
        if(splite){
            int index = timeStr.indexOf(" ");
            if(index>0){
                timeStr = timeStr.replace(" ","\n");
            }
            return timeStr;
        }
        return timeStr;
    }

    public static String formatHourseDate(Context context, long dateInMillis) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);

        String format = null;
        if (DateFormat.is24HourFormat(context)) {
            format = "HH:mm";
        } else {
            format = "hh:mm a";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String dateStr = sdf.format(calendar.getTime());

        return dateStr;
    }
}