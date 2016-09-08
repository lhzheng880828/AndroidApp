/****************************************************************************
 *
 * FILENAME:        com.base.module.service.DownService.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-2-6
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
package com.base.module.pack.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.base.module.language.GuiDisplay;
import com.base.module.pack.R;
import com.base.module.pack.main.AppActivity;
import com.base.module.pack.main.Download;
import com.base.module.pack.main.ExpandActivity;
import com.base.module.pack.method.PackMethod;
public class FailNotice {
    protected static final String TAG = "FailNotice";
    private static Context mContext;
    protected int mNotice_id = 2;
    private Notification mDownNotification = null;
    private Notification.Builder notification = null;
    private NotificationManager mManager = null;
    private GuiDisplay mGuidisplay;

    public FailNotice(Context context) {
        mContext = context;
        mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mGuidisplay = GuiDisplay.instance();
    }

    public void setFailNotice(int count) {
        if(count<=0 && mManager!=null){
            mManager.cancel(mNotice_id);
        }else{
            String showtext = count + PackMethod.getSpace() + mGuidisplay.getValue(mContext, 3352);
            if(mDownNotification==null){
                Intent intent = new Intent();
                intent.setClass(mContext, ExpandActivity.class);
                intent.putExtra(AppActivity.EXPANDFLAG, AppActivity.DOWNFLAG);

                notification = new Notification.Builder(mContext);
                notification.setTicker(showtext)
                .setContentTitle(mGuidisplay.getValue(mContext, 3353) + ": ")
                .setContentText(showtext)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.download_failed)
                .setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0));
                mDownNotification = notification.build();
                mDownNotification.icon = R.drawable.download_failed_panel;
            }else if(notification!=null){
                notification.setTicker(showtext)
                .setContentText(showtext);
                mDownNotification = notification.build();
            }
            mManager.notify(mNotice_id, mDownNotification);
        }
    }

    /**
     * where is go when click the notice
     * @param moodId
     * @return
     */
    private PendingIntent receiveMsmIntent() {
        /**The PendingIntent to launch our activity if the user selects this
        notification. Note the use of FLAG_UPDATE_CURRENT so that if there
       is already an active matching pending intent, we will update its
       extras to be the ones passed in here.*/
        Intent intent = new Intent();
        intent.setClass(mContext, ExpandActivity.class);
        intent.putExtra(AppActivity.EXPANDFLAG, AppActivity.DOWNFLAG);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }
}