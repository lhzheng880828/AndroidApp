package com.base.module.pack.service;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import com.base.module.pack.common.Log;

import java.util.Iterator;
import java.util.Set;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 * @author zhu
 *
 */
public class DownReceiver extends BroadcastReceiver {
    static final String DOWN_ACTION = "com.base.module.pack.intent.action.DOWN_ACTION";
    protected static final String TAG = "DownReceiver";
    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;
    private static DownReceiver mInstance;

    public static DownReceiver getInstance() {
        if (mInstance == null) {
            mInstance = new DownReceiver();
        }
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DOWN_ACTION)) {
            intent.setClass(context, DownService.class);
            Bundle bundle = intent.getExtras();
            Set<String> set = bundle.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Log.d("Key : " +key + "  value:" +bundle.get(key));
            }
            context.startService(intent);
        }
    }

    /**
     * Start the service to process the current event notifications, acquiring
     * the wake lock before returning to ensure that the service will run. begin
     * service
     */
    public static void beginStartingService(Context context, Intent intent) {
        Log.d("  beginStartingService intent "+intent);
        synchronized (mStartingServiceSync) {
            if (mStartingService == null) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StartingAlertService");
                mStartingService.setReferenceCounted(false);
            }
            mStartingService.acquire();
            context.startService(intent);
        }
    }

    /**
     * Called back by the service when it has finished processing notifications,
     * releasing the wake lock if the service is now stopping.
     */
    public static void finishStartingService(Service service, int startId) {
        synchronized (mStartingServiceSync) {
            if (mStartingService != null) {
                if (service.stopSelfResult(startId)) {
                    mStartingService.release();
                }
            }
        }
    }
}