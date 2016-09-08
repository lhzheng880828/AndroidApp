/****************************************************************************
 *
 * FILENAME:        com.base.module.method.Time.java
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
package com.base.module.pack.method;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
public class MyTime {
    protected static final String TAG = "MyTime";

    public static String getdate() {
        Date date = new Date(System.currentTimeMillis());
        return String.valueOf(date.getTime());
    }

    public String timeShow(Context context, String time) {
        if (time == null) {
            return "00-00-00";
        }
        try {
            Date t_date = new Date(Long.parseLong(time));
            Date n_date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat;
            SimpleDateFormat dayFormat;
            SimpleDateFormat yearFormat;
            if ((t_date.getYear() - n_date.getYear()) > 0) {
                yearFormat = new SimpleDateFormat("yyyy:");
                return yearFormat.format(t_date);
            } else if ((t_date.getMonth() != n_date.getMonth())
                    || t_date.getDate() != n_date.getDate()) {
                dateFormat = new SimpleDateFormat("MM:dd:");
                return dateFormat.format(t_date);
            } else {
                ContentResolver cv = context.getContentResolver();
                String strTimeFormat = android.provider.Settings.System.getString(cv,
                        android.provider.Settings.System.TIME_12_24);
                if (strTimeFormat == null || strTimeFormat.equals("12")) {
                    dayFormat = new SimpleDateFormat("a: KK:mm");

                } else {
                    dayFormat = new SimpleDateFormat("HH : mm");
                }
                return dayFormat.format(t_date);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return "00-00-00";
    }

    public String getDetailTime(Context context, String time) {
        if (time == null) {
            return "00-00-00";
        }
        try {
            Date t_date = new Date(Long.parseLong(time));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            return dateFormat.format(t_date);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return "00-00-00";
    }
}