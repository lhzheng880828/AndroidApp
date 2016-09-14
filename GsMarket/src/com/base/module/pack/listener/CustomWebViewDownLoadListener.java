package com.base.module.pack.listener;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.base.module.pack.R;
import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.GetApkInfo;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.Log;
import com.base.module.pack.common.ThreadManager;
import com.base.module.pack.common.Utils;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;

/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 * User: czheng
 * Date: 5/7/13
 * Time: 6:46 PM
 */
public class CustomWebViewDownLoadListener implements DownloadListener {

    private Context context;

    private  Toast mToast;


    public CustomWebViewDownLoadListener(Activity activity) {
        context = activity.getBaseContext();
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, final long contentLength) {
        Log.d("clickToDown onDownloadStart"+url);
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                clickToDown(url,null, context,contentLength);
                Looper.loop();
            }
        }.start();
    }

    private  synchronized void clickToDown(String url,String iconUrl,Context context,long fileSize) {
        Log.d("clickToDown:"+url);
        if (PackMethod.isValid(url, false)) {
            ApkInfo apkinfo = GetApkInfo.getApkInfo(url);
            if (apkinfo != null) {
                apkinfo.setFilesize(fileSize);
                PackDaoInter packDao = PackDao.getInstance(context);
                if (packDao != null && !packDao.isOpen()) {
                    packDao.open();
                }
                try {
                    int state = 0;
                    String new_version = apkinfo.getVersion();
                    String  now_version =  Utils.getAppVersion(context,apkinfo.getPackageName());
                    Pack findPack = packDao.findByLName(apkinfo.getPackageName());
                    if (findPack.getIsFind()) {
                        state = findPack.getPackState();
                        //now_version = findPack.getPackVersion();
                    }
                    if (state < Pack.STATE_WAIT) {//not installed
                        downLoad(url,iconUrl, apkinfo,context);
                    } else if (state < Pack.STATE_INSTALL) {//in down or installing
                        Toast.makeText(context,
                                apkinfo.getAppname() + Utils.getSpace()
                                + context.getString(R.string.pkg_exist),
                                Toast.LENGTH_LONG).show();
                    } else if (now_version != null && new_version != null && !now_version.equals(new_version)) { //update
                        PackMethod.update(context, apkinfo.getPackageName(), url, apkinfo);
                    } else { //already exist
                        showToast(context.getString(R.string.app_exist), context);

                    }
                } catch (Exception e) {
                    Log.e(e.toString());
                    e.printStackTrace();
                } finally {
                    packDao.close();
                }
            } else {
                showToast(context.getString(R.string.fail_get_apk_info),context);
            }
        } else {
            showToast(context.getString(R.string.url_error),context);
        }
    }

    private  void showToast(String text,Context context) {
        mToast.setText(text);
        mToast.show();
    }

    private  void downLoad(String url,String iconUrl, ApkInfo apkinfo, Context context) {
        Log.d("Download ------"+url+" "+apkinfo);
        PackMethod.sendDownloadBroader(context, url,iconUrl, apkinfo);
    }
    public  void downApp(final String url,final String iconUrl,final Context context,final long fileSize){
        ThreadManager.execute(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                clickToDown(url,iconUrl, context,fileSize);
                Looper.loop();
            }
        });
    }
}