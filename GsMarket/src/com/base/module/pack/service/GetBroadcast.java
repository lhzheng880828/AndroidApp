package com.base.module.pack.service;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
public class GetBroadcast extends BroadcastReceiver {
    private static GetBroadcast mReceiver = new GetBroadcast();
    protected static final String TAG = "GetBroadcast";
    private static IntentFilter mIntentFilter;
    private static PackDaoInter mPackDao;

    public static void registerReceiver(Context context) {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addDataScheme("package");
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        mIntentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        context.registerReceiver(mReceiver, mIntentFilter);
    }

    public static void unregisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (mPackDao == null)
            mPackDao = PackDao.getInstance(context);
        else if (!mPackDao.isOpen())
            mPackDao.open();
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            final String packageName = intent.getData().getSchemeSpecificPart();
            new Thread(){

                @Override
                public void run() {
                    mPackDao.installByLname(packageName);
                    PackMethod.sendRefreshBroader(context);
                }

            }.start();

        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            final String packageName = intent.getData().getSchemeSpecificPart();
            new Thread(){

                @Override
                public void run() {
                    mPackDao.uninstallByLname(packageName);
                    PackMethod.sendRefreshBroader(context);
                }					
            }.start();

        }else if("android.intent.action.BOOT_COMPLETED".equals(action)){
            new Thread(){

                @Override
                public void run() {

                    mPackDao.processDownloadTask();
                }


            }.start();


        }
    }
}