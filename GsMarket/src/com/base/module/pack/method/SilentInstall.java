/****************************************************************************
 *
 * FILENAME:        com.test.install.SilentInstall.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-2-21
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

import java.io.File;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.Queue;
import com.base.module.pack.service.DownService.DownloaderTask;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.content.pm.IPackageInstallObserver;
public class SilentInstall {
    protected static final String TAG = "SilentInstall";
    private Context mContext;
    private Handler mHandler;
    private DownloaderTask mTask;

    public static final int INSTALL_COMPLETE = 1;
    //public static final int SUCCEEDED = 1;
    //public static final int FAILED = 0;

    private static Queue<SilentInstall> silentInstalls = new LinkedList<SilentInstall>();
    private static Thread mThread = null;
    private static boolean mFlag;

    public SilentInstall(Context context, DownloaderTask task, Handler handler) {
        mContext = context;
        mHandler = handler;
        mTask = task;
    }


    public static void endThread() {
        mFlag = false;
        if(mThread!=null){
            synchronized (mThread) {
                mThread.notify();
            }
        }
    }

    public static void startInstall(SilentInstall install) {
        mFlag = true;
        silentInstalls.add(install);
        if (mThread == null || mThread.getState() == Thread.State.TERMINATED) {
            mThread = new Thread() {
                @Override
                public void run() {
                    while (mFlag) {

                        if (!silentInstalls.isEmpty()) {
                            SilentInstall silentInstall = silentInstalls.poll();

                            int installFlags = PackageManager.INSTALL_INTERNAL;
                            PackageManager pm = silentInstall.mContext.getPackageManager();
                            try {
                                PackageInfo pi = pm.getPackageInfo(silentInstall.mTask.getPack().getPackLName(),
                                        PackageManager.GET_UNINSTALLED_PACKAGES);
                                if (pi != null) {
                                    //Log.i(TAG, "--------!null-------");
                                    installFlags = PackageManager.INSTALL_REPLACE_EXISTING;
                                }else{
                                    //Log.i(TAG, "--------=null-------");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, " SilentInstallActivity e..." + e.toString());
                                e.printStackTrace();
                            }
                            //Intent intent = new Intent();
                            String installerPackageName = silentInstall.mTask.getPack().getPackLName();//; intent.getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);
                            PackageInstallObserver observer = silentInstall.new PackageInstallObserver();
                            String packCode = silentInstall.mTask.getPack().getPackSerial();
                            String path = URLDecoder.decode(silentInstall.mTask.getPack().getPackNationRoute() + "/"
                                    + packCode);
                            File file = new File(path);
                            Uri uri = Uri.fromFile(file);
                            pm.installPackage(uri, observer, installFlags, installerPackageName);						
                        }

                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
        }
        if (mThread.getState() == Thread.State.NEW) {
            mThread.start();
        }else{
            synchronized (mThread) {
                mThread.notify();
            }
        }


    }

    class PackageInstallObserver extends IPackageInstallObserver.Stub {
        @Override
        public void packageInstalled(String packageName, int returnCode) {
            Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
            msg.arg2 = returnCode;
            msg.arg1 = mTask.getTaskId();
            msg.obj = mTask;
            mHandler.sendMessage(msg);
            Log.e(mTask.getPack().getPackName(), "install complete!!!");
        }
    };
}